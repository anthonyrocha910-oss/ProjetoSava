const express = require('express');
const fs = require('fs');
const path = require('path');
const {
  createHash,
  randomBytes,
  scrypt: scryptCallback,
  timingSafeEqual
} = require('crypto');
const { promisify } = require('util');

require('dotenv').config();

const scrypt = promisify(scryptCallback);
const app = express();
const PORT = process.env.PORT || 3000;
const SUBJECTS = [
  'Matemática',
  'Ciências',
  'Português',
  'História',
  'Educação Física'
];
const DATA_DIR = path.join(__dirname, 'data');
const DATA_FILE = path.join(DATA_DIR, 'storage.json');

const schedule = [
  { day: 'Segunda-feira', time: '08:00 - 09:30', subject: 'Matemática' },
  { day: 'Terça-feira', time: '10:00 - 11:30', subject: 'Ciências' },
  { day: 'Quarta-feira', time: '08:00 - 09:30', subject: 'Português' },
  { day: 'Quinta-feira', time: '09:45 - 11:15', subject: 'História' },
  { day: 'Sexta-feira', time: '07:30 - 09:00', subject: 'Educação Física' }
];

function getLocalDateKey(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

const defaultTeachers = [
  { nomeCompleto: 'Maria da Silva', cpf: '52998224725', materia: 'Matemática', senha: '1234' }
];

const defaultStudents = [
  {
    nomeCompleto: 'Ana Souza',
    ra: '1001',
    senha: '1234',
    registros: {
      Matemática: 'presente',
      Português: 'presente',
      História: 'falta',
      Ciências: 'falta',
      'Educação Física': 'falta'
    }
  },
  {
    nomeCompleto: 'Bruno Lima',
    ra: '1002',
    senha: '1234',
    registros: {
      Matemática: 'falta',
      Ciências: 'presente',
      Português: 'falta',
      História: 'falta',
      'Educação Física': 'presente'
    }
  }
];

let storage = { teachers: [], students: [] };

function documentId(value) {
  return createHash('sha256').update(String(value)).digest('hex');
}

async function hashPassword(password) {
  const senhaSalt = randomBytes(16).toString('hex');
  const senhaHash = await scrypt(password, senhaSalt, 64);
  return { senhaHash: senhaHash.toString('hex'), senhaSalt };
}

async function verifyPassword(password, user) {
  if (!user.senhaHash || !user.senhaSalt) {
    return false;
  }

  const actualHash = await scrypt(password, user.senhaSalt, 64);
  const expectedHash = Buffer.from(user.senhaHash, 'hex');
  return actualHash.length === expectedHash.length
    && timingSafeEqual(actualHash, expectedHash);
}

function normalizeStatus(value) {
  return value === 'presente' ? 'presente' : 'falta';
}

function makeRecordsMap(records) {
  const result = {};
  if (!records) {
    return result;
  }

  Object.entries(records).forEach(([subject, status]) => {
    result[subject] = normalizeStatus(status);
  });

  return result;
}

function normalizeCpf(value) {
  return String(value || '').replace(/\D/g, '');
}

function isValidCpf(cpf) {
  if (!/^\d{11}$/.test(cpf) || /^(\d)\1{10}$/.test(cpf)) {
    return false;
  }

  const calculateDigit = (digits) => {
    const sum = digits
      .split('')
      .map(Number)
      .reduce((total, digit, index) => total + digit * (digits.length + 1 - index), 0);
    const remainder = (sum * 10) % 11;
    return remainder === 10 ? 0 : remainder;
  };

  return calculateDigit(cpf.slice(0, 9)) === Number(cpf[9])
    && calculateDigit(cpf.slice(0, 10)) === Number(cpf[10]);
}

function readStorage() {
  try {
    const raw = fs.readFileSync(DATA_FILE, 'utf8');
    const data = JSON.parse(raw);
    storage = {
      teachers: Array.isArray(data.teachers) ? data.teachers : [],
      students: Array.isArray(data.students) ? data.students : []
    };
  } catch {
    storage = { teachers: [], students: [] };
    writeStorage();
  }
}

function writeStorage() {
  fs.mkdirSync(DATA_DIR, { recursive: true });
  fs.writeFileSync(DATA_FILE, JSON.stringify(storage, null, 2));
}

async function seedDefaultData() {
  for (const teacher of defaultTeachers) {
    const exists = storage.teachers.some((item) => normalizeCpf(item.cpf) === normalizeCpf(teacher.cpf));
    if (!exists) {
      const { senha, ...teacherData } = teacher;
      storage.teachers.push({
        ...teacherData,
        ...(await hashPassword(senha))
      });
    }
  }

  for (const student of defaultStudents) {
    const exists = storage.students.some((item) => String(item.ra) === String(student.ra));
    if (!exists) {
      const { senha, ...studentData } = student;
      storage.students.push({
        ...studentData,
        ...(await hashPassword(senha)),
        registros: { ...studentData.registros }
      });
    }
  }

  storage.students.forEach((student) => {
    if (!Array.isArray(student.attendanceHistory)) {
      student.attendanceHistory = [];
    }
  });

  writeStorage();
}

async function getTeachers() {
  return [...storage.teachers]
    .sort((a, b) => a.nomeCompleto.localeCompare(b.nomeCompleto, 'pt-BR'))
    .map((teacher) => ({
      id: documentId(teacher.cpf),
      nomeCompleto: teacher.nomeCompleto,
      materia: teacher.materia
    }));
}

async function getStudents() {
  const today = getLocalDateKey();
  return [...storage.students]
    .sort((a, b) => a.nomeCompleto.localeCompare(b.nomeCompleto, 'pt-BR'))
    .map((student) => {
      const todayRecords = (student.attendanceHistory || [])
        .filter((record) => record.date === today)
        .reduce((records, record) => ({ ...records, [record.subject]: record.status }), {});
      return {
        id: documentId(student.ra),
        nomeCompleto: student.nomeCompleto,
        ra: student.ra,
        registros: todayRecords,
        attendanceHistory: student.attendanceHistory || []
      };
    });
}

async function teacherRegister({ nomeCompleto, cpf, materia, senha }) {
  const cleanedCpf = normalizeCpf(cpf);
  const cleanedPassword = String(senha || '').trim();
  const cleanedName = String(nomeCompleto || '').trim();

  if (!cleanedName || !cleanedCpf || !cleanedPassword || !materia) {
    throw new Error('Preencha todos os campos do professor.');
  }

  if (!isValidCpf(cleanedCpf)) {
    throw new Error('Digite um CPF válido.');
  }

  if (!SUBJECTS.includes(materia)) {
    throw new Error('Selecione uma matéria válida.');
  }

  const exists = storage.teachers.some((teacher) => normalizeCpf(teacher.cpf) === cleanedCpf);
  if (exists) {
    throw new Error('Esse CPF já foi cadastrado para um professor.');
  }

  const professor = {
    nomeCompleto: cleanedName,
    cpf: cleanedCpf,
    materia,
    ...(await hashPassword(cleanedPassword))
  };

  storage.teachers.push(professor);
  writeStorage();

  return {
    nomeCompleto: professor.nomeCompleto,
    materia: professor.materia
  };
}

async function studentRegister({ nomeCompleto, ra, senha }) {
  const cleanedRa = String(ra || '').trim();
  const cleanedPassword = String(senha || '').trim();
  const cleanedName = String(nomeCompleto || '').trim();

  if (!cleanedName || !cleanedRa || !cleanedPassword) {
    throw new Error('Preencha todos os campos do aluno.');
  }

  const exists = storage.students.some((student) => String(student.ra) === cleanedRa);
  if (exists) {
    throw new Error('Esse RA já foi cadastrado para um aluno.');
  }

  const newStudent = {
    nomeCompleto: cleanedName,
    ra: cleanedRa,
    ...(await hashPassword(cleanedPassword)),
    registros: {},
    attendanceHistory: []
  };

  storage.students.push(newStudent);
  writeStorage();

  return {
    id: documentId(newStudent.ra),
    nomeCompleto: newStudent.nomeCompleto,
    ra: newStudent.ra,
    registros: newStudent.registros,
    attendanceHistory: newStudent.attendanceHistory
  };
}

async function getTeacherByCredentials(cpf, senha) {
  const cleanedCpf = normalizeCpf(cpf);
  const cleanedPassword = String(senha || '').trim();
  const teacher = storage.teachers.find((item) => normalizeCpf(item.cpf) === cleanedCpf);

  return teacher && await verifyPassword(cleanedPassword, teacher)
    ? { nomeCompleto: teacher.nomeCompleto, materia: teacher.materia }
    : null;
}

async function getStudentByCredentials(ra, senha) {
  const cleanedRa = String(ra || '').trim();
  const cleanedPassword = String(senha || '').trim();
  const student = storage.students.find((item) => String(item.ra) === cleanedRa);

  return student && await verifyPassword(cleanedPassword, student)
    ? {
        id: documentId(student.ra),
        nomeCompleto: student.nomeCompleto,
        ra: student.ra,
        registros: makeRecordsMap(student.registros || {}),
        attendanceHistory: student.attendanceHistory || []
      }
    : null;
}

async function updateAttendance({ studentRa, subject, status }) {
  const cleanedSubject = String(subject || '').trim();
  const cleanedRa = String(studentRa || '').trim();

  if (!SUBJECTS.includes(cleanedSubject)) {
    throw new Error('Matéria inválida.');
  }

  if (!['present', 'absent'].includes(status)) {
    throw new Error('Status de presença inválido.');
  }

  if (!cleanedRa || !cleanedSubject) {
    throw new Error('Dados da presença inválidos.');
  }

  const student = storage.students.find((item) => String(item.ra) === cleanedRa);
  if (!student) {
    throw new Error('Aluno não encontrado.');
  }

  const now = new Date();
  const date = getLocalDateKey(now);
  const attendanceHistory = Array.isArray(student.attendanceHistory) ? student.attendanceHistory : [];
  const existingRecord = attendanceHistory.find(
    (record) => record.subject === cleanedSubject && record.date === date
  );
  const updatedRecord = {
    subject: cleanedSubject,
    status: status === 'present' ? 'presente' : 'falta',
    date,
    recordedAt: now.toISOString()
  };
  if (existingRecord) {
    Object.assign(existingRecord, updatedRecord);
  } else {
    attendanceHistory.push(updatedRecord);
  }
  student.attendanceHistory = attendanceHistory;
  student.registros = { ...(student.registros || {}), [cleanedSubject]: updatedRecord.status };
  writeStorage();
  return { success: true };
}

async function resetAttendanceForSubject(subject) {
  const cleanedSubject = String(subject || '').trim();
  if (!SUBJECTS.includes(cleanedSubject)) {
    throw new Error('Matéria inválida.');
  }

  const today = getLocalDateKey();
  storage.students.forEach((student) => {
    student.attendanceHistory = (student.attendanceHistory || []).filter(
      (record) => record.subject !== cleanedSubject || record.date !== today
    );
  });

  writeStorage();
  return { success: true };
}

async function startServer() {
  readStorage();
  await seedDefaultData();

  app.listen(PORT, () => {
    console.log(`Servidor local em http://localhost:${PORT}`);
  });
}

function parseJsonBody(req, res, next) {
  express.json()(req, res, next);
}

app.use(parseJsonBody);
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') {
    return res.sendStatus(200);
  }
  next();
});

app.get('/api/health', (req, res) => {
  res.json({ ok: true, storage: 'local' });
});

app.get('/api/schedule', (req, res) => {
  res.json(schedule);
});

app.get('/api/teachers', async (req, res) => {
  try {
    const teachers = await getTeachers();
    res.json(teachers);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.post('/api/teachers/register', async (req, res) => {
  try {
    const teacher = await teacherRegister(req.body || {});
    res.status(201).json({ teacher });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.post('/api/teachers/login', async (req, res) => {
  try {
    const teacher = await getTeacherByCredentials((req.body || {}).cpf, (req.body || {}).senha);

    if (!teacher) {
      return res.status(401).json({ message: 'RA ou senha do professor incorretos.' });
    }

    res.json({ teacher });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.get('/api/students', async (req, res) => {
  try {
    const students = await getStudents();
    res.json(students);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.post('/api/students/register', async (req, res) => {
  try {
    const student = await studentRegister(req.body || {});
    res.status(201).json({ student });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.post('/api/students/login', async (req, res) => {
  try {
    const student = await getStudentByCredentials((req.body || {}).ra, (req.body || {}).senha);

    if (!student) {
      return res.status(401).json({ message: 'RA ou senha do aluno incorretos.' });
    }

    res.json({ student });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.put('/api/attendance', async (req, res) => {
  try {
    const result = await updateAttendance(req.body || {});
    res.json(result);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.post('/api/attendance/reset', async (req, res) => {
  try {
    const result = await resetAttendanceForSubject(req.body?.subject);
    res.json(result);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

app.use(express.static(__dirname));
app.get('*', (req, res) => {
  if (req.path.startsWith('/api/')) {
    return res.status(404).json({ message: 'Rota da API não encontrada.' });
  }
  res.sendFile(path.join(__dirname, 'index.html'));
});

startServer().catch((error) => {
  console.error('Não foi possível iniciar o servidor local:', error);
  process.exitCode = 1;
});
