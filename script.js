const schedule = [
  { day: 'Segunda-feira', time: '08:00 - 09:30', subject: 'Matemática' },
  { day: 'Terça-feira', time: '10:00 - 11:30', subject: 'Ciências' },
  { day: 'Quarta-feira', time: '08:00 - 09:30', subject: 'Português' },
  { day: 'Quinta-feira', time: '09:45 - 11:15', subject: 'História' },
  { day: 'Sexta-feira', time: '07:30 - 09:00', subject: 'Educação Física' }
];

const usesBrowserStorage = window.location.protocol === 'file:'
  || !(['localhost', '127.0.0.1'].includes(window.location.hostname)
    && window.location.port === '3000');
const API_ORIGIN = window.location.origin;
const API_BASE = `${API_ORIGIN}/api`;
const LOCAL_STORAGE_KEY = 'controle-presenca-escolar';
const subjects = ['Matemática', 'Ciências', 'Português', 'História', 'Educação Física'];
const studentDashboardSubjects = ['Matemática', 'Português', 'História', 'Educação Física', 'Ciências'];
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

function getLocalDateKey(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

const teacherAccessBtn = document.getElementById('teacherAccessBtn');
const studentAccessBtn = document.getElementById('studentAccessBtn');
const teacherRegisterHomeBtn = document.getElementById('teacherRegisterHomeBtn');
const studentRegisterHomeBtn = document.getElementById('studentRegisterHomeBtn');
const teacherNavBtn = document.getElementById('teacherNavBtn');
const studentNavBtn = document.getElementById('studentNavBtn');
const homeNav = document.getElementById('homeNav');

const teacherRegisterChoiceBtn = document.getElementById('teacherRegisterChoiceBtn');
const teacherLoginChoiceBtn = document.getElementById('teacherLoginChoiceBtn');
const studentRegisterChoiceBtn = document.getElementById('studentRegisterChoiceBtn');
const studentLoginChoiceBtn = document.getElementById('studentLoginChoiceBtn');
const backTeacherToAccess = document.getElementById('backTeacherToAccess');
const backStudentToAccess = document.getElementById('backStudentToAccess');

const backTeacherFromRegister = document.getElementById('backTeacherFromRegister');
const backTeacherFromLogin = document.getElementById('backTeacherFromLogin');
const backStudentFromRegister = document.getElementById('backStudentFromRegister');
const backStudentFromLogin = document.getElementById('backStudentFromLogin');

const teacherRegisterForm = document.getElementById('teacherRegisterForm');
const teacherLoginForm = document.getElementById('teacherLoginForm');
const studentRegisterForm = document.getElementById('studentRegisterForm');
const studentLoginForm = document.getElementById('studentLoginForm');

const userBadge = document.getElementById('userBadge');
const teacherTitle = document.getElementById('teacherTitle');
const teacherSubjectFilter = document.getElementById('teacherSubjectFilter');
const teacherScheduleList = document.getElementById('teacherScheduleList');
const studentList = document.getElementById('studentList');
const presentCount = document.getElementById('presentCount');
const absentCount = document.getElementById('absentCount');
const attendancePercent = document.getElementById('attendancePercent');
const resetBtn = document.getElementById('resetBtn');
const studentNameDisplay = document.getElementById('studentNameDisplay');
const studentRaDisplay = document.getElementById('studentRaDisplay');
const studentPresentCount = document.getElementById('studentPresentCount');
const studentAbsentCount = document.getElementById('studentAbsentCount');
const studentAttendancePercent = document.getElementById('studentAttendancePercent');
const studentRecordsTable = document.getElementById('studentRecordsTable');
const studentLessonsCount = document.getElementById('studentLessonsCount');
const studentUpdatedDate = document.getElementById('studentUpdatedDate');
const studentAttendanceAlert = document.getElementById('studentAttendanceAlert');
const studentRecentAbsences = document.getElementById('studentRecentAbsences');
const attendanceAssistantForm = document.getElementById('attendanceAssistantForm');
const attendanceAssistantQuestion = document.getElementById('attendanceAssistantQuestion');
const assistantConversation = document.getElementById('assistantConversation');

let currentUser = null;

async function apiRequest(path, options = {}) {
  if (usesBrowserStorage) {
    return browserStorageRequest(path, options);
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      }
    });
  } catch {
    throw new Error('Não foi possível concluir a operação. Verifique sua conexão e tente novamente.');
  }

  let payload;
  try {
    payload = await response.json();
  } catch {
    throw new Error('O servidor retornou uma resposta inválida. Tente novamente.');
  }

  if (!response.ok) {
    throw new Error(payload.message || 'Erro ao realizar a operação.');
  }

  return payload;
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

function bytesToHex(bytes) {
  return Array.from(bytes, (byte) => byte.toString(16).padStart(2, '0')).join('');
}

async function hashBrowserPassword(password, salt) {
  if (!window.crypto || !window.crypto.subtle) {
    throw new Error('Seu navegador não oferece armazenamento seguro de senhas. Abra o sistema em um navegador atualizado.');
  }

  const passwordSalt = salt || bytesToHex(window.crypto.getRandomValues(new Uint8Array(16)));
  const key = await window.crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(password),
    'PBKDF2',
    false,
    ['deriveBits']
  );
  const hash = await window.crypto.subtle.deriveBits(
    { name: 'PBKDF2', salt: Uint8Array.from(passwordSalt.match(/.{2}/g), (byte) => parseInt(byte, 16)), iterations: 100000, hash: 'SHA-256' },
    key,
    256
  );

  return { passwordSalt, passwordHash: bytesToHex(new Uint8Array(hash)) };
}

async function verifyBrowserPassword(password, user) {
  if (!user.passwordSalt || !user.passwordHash) {
    return false;
  }

  const { passwordHash } = await hashBrowserPassword(password, user.passwordSalt);
  if (passwordHash.length !== user.passwordHash.length) {
    return false;
  }

  let difference = 0;
  for (let index = 0; index < passwordHash.length; index += 1) {
    difference |= passwordHash.charCodeAt(index) ^ user.passwordHash.charCodeAt(index);
  }
  return difference === 0;
}

function saveBrowserDatabase(database) {
  window.localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(database));
}

async function loadBrowserDatabase() {
  const raw = window.localStorage.getItem(LOCAL_STORAGE_KEY);
  if (raw !== null) {
    let database;
    try {
      database = JSON.parse(raw);
    } catch {
      throw new Error('Os dados salvos neste navegador estão inválidos. Não foi possível carregar o sistema.');
    }
    if (!Array.isArray(database.teachers) || !Array.isArray(database.students)) {
      throw new Error('Os dados salvos neste navegador estão inválidos. Não foi possível carregar o sistema.');
    }
    database.students.forEach((student) => {
      if (!Array.isArray(student.attendanceHistory)) {
        student.attendanceHistory = [];
      }
    });
    return database;
  }

  const database = { teachers: [], students: [] };
  for (const teacher of defaultTeachers) {
    const { passwordHash, passwordSalt } = await hashBrowserPassword(teacher.senha);
    database.teachers.push({
      nomeCompleto: teacher.nomeCompleto,
      cpf: teacher.cpf,
      materia: teacher.materia,
      passwordHash,
      passwordSalt
    });
  }
  for (const student of defaultStudents) {
    const { passwordHash, passwordSalt } = await hashBrowserPassword(student.senha);
    database.students.push({
      nomeCompleto: student.nomeCompleto,
      ra: student.ra,
      passwordHash,
      passwordSalt,
      registros: { ...student.registros },
      attendanceHistory: []
    });
  }
  saveBrowserDatabase(database);
  return database;
}

async function browserStorageRequest(path, options) {
  const database = await loadBrowserDatabase();
  const body = options.body ? JSON.parse(options.body) : {};

  if (path === '/health') {
    return { ok: true, storage: 'browser' };
  }
  if (path === '/students' && (!options.method || options.method === 'GET')) {
    const today = getLocalDateKey();
    return [...database.students]
      .sort((a, b) => a.nomeCompleto.localeCompare(b.nomeCompleto, 'pt-BR'))
      .map(({ nomeCompleto, ra, attendanceHistory = [] }) => ({
        nomeCompleto,
        ra,
        registros: attendanceHistory
          .filter((record) => record.date === today)
          .reduce((records, record) => ({ ...records, [record.subject]: record.status }), {}),
        attendanceHistory
      }));
  }
  if (path === '/teachers/register' && options.method === 'POST') {
    const nomeCompleto = String(body.nomeCompleto || '').trim();
    const cpf = normalizeCpf(body.cpf);
    const materia = String(body.materia || '').trim();
    const senha = String(body.senha || '').trim();
    if (!nomeCompleto || !cpf || !senha || !materia) {
      throw new Error('Preencha todos os campos do professor.');
    }
    if (!isValidCpf(cpf)) {
      throw new Error('Digite um CPF válido.');
    }
    if (!subjects.includes(materia)) {
      throw new Error('Selecione uma matéria válida.');
    }
    if (database.teachers.some((teacher) => normalizeCpf(teacher.cpf) === cpf)) {
      throw new Error('Esse CPF já foi cadastrado para um professor.');
    }
    const password = await hashBrowserPassword(senha);
    database.teachers.push({ nomeCompleto, cpf, materia, ...password });
    saveBrowserDatabase(database);
    return { teacher: { nomeCompleto, materia } };
  }
  if (path === '/teachers/login' && options.method === 'POST') {
    const teacher = database.teachers.find((item) => normalizeCpf(item.cpf) === normalizeCpf(body.cpf));
    if (!teacher || !await verifyBrowserPassword(String(body.senha || '').trim(), teacher)) {
      throw new Error('CPF ou senha do professor incorretos.');
    }
    return { teacher: { nomeCompleto: teacher.nomeCompleto, materia: teacher.materia } };
  }
  if (path === '/students/register' && options.method === 'POST') {
    const nomeCompleto = String(body.nomeCompleto || '').trim();
    const ra = String(body.ra || '').trim();
    const senha = String(body.senha || '').trim();
    if (!nomeCompleto || !ra || !senha) {
      throw new Error('Preencha todos os campos do aluno.');
    }
    if (database.students.some((student) => String(student.ra) === ra)) {
      throw new Error('Esse RA já foi cadastrado para um aluno.');
    }
    const password = await hashBrowserPassword(senha);
    const student = {
      nomeCompleto,
      ra,
      ...password,
      registros: {},
      attendanceHistory: []
    };
    database.students.push(student);
    saveBrowserDatabase(database);
    return {
      student: {
        nomeCompleto,
        ra,
        registros: student.registros,
        attendanceHistory: student.attendanceHistory
      }
    };
  }
  if (path === '/students/login' && options.method === 'POST') {
    const student = database.students.find((item) => String(item.ra) === String(body.ra || '').trim());
    if (!student || !await verifyBrowserPassword(String(body.senha || '').trim(), student)) {
      throw new Error('RA ou senha do aluno incorretos.');
    }
    return {
      student: {
        nomeCompleto: student.nomeCompleto,
        ra: student.ra,
        registros: student.registros || {},
        attendanceHistory: student.attendanceHistory || []
      }
    };
  }
  if (path === '/attendance' && options.method === 'PUT') {
    const subject = String(body.subject || '').trim();
    const student = database.students.find((item) => String(item.ra) === String(body.studentRa || '').trim());
    if (!subjects.includes(subject)) {
      throw new Error('Matéria inválida.');
    }
    if (!['present', 'absent'].includes(body.status)) {
      throw new Error('Status de presença inválido.');
    }
    if (!student) {
      throw new Error('Aluno não encontrado.');
    }
    const now = new Date();
    const date = getLocalDateKey(now);
    const attendanceHistory = Array.isArray(student.attendanceHistory) ? student.attendanceHistory : [];
    const updatedRecord = {
      subject,
      status: body.status === 'present' ? 'presente' : 'falta',
      date,
      recordedAt: now.toISOString()
    };
    const existingRecord = attendanceHistory.find(
      (record) => record.subject === subject && record.date === date
    );
    if (existingRecord) {
      Object.assign(existingRecord, updatedRecord);
    } else {
      attendanceHistory.push(updatedRecord);
    }
    student.attendanceHistory = attendanceHistory;
    student.registros = { ...(student.registros || {}), [subject]: updatedRecord.status };
    saveBrowserDatabase(database);
    return { success: true };
  }
  if (path === '/attendance/reset' && options.method === 'POST') {
    const subject = String(body.subject || '').trim();
    if (!subjects.includes(subject)) {
      throw new Error('Matéria inválida.');
    }
    const today = getLocalDateKey();
    database.students.forEach((student) => {
      student.attendanceHistory = (student.attendanceHistory || []).filter(
        (record) => record.subject !== subject || record.date !== today
      );
    });
    saveBrowserDatabase(database);
    return { success: true };
  }

  throw new Error('Operação não disponível no armazenamento deste navegador.');
}

function showScreen(screenId) {
  document.querySelectorAll('.screen').forEach((screen) => screen.classList.add('hidden'));
  const target = document.getElementById(screenId);
  if (target) {
    target.classList.remove('hidden');
  }
  homeNav.hidden = screenId !== 'accessScreen';
  userBadge.hidden = !['teacherDashboard', 'studentDashboard'].includes(screenId);
}

function setUserBadge(text) {
  userBadge.textContent = text;
}

function renderTeacherSchedule(selectedSubject = 'Matemática') {
  const subjectToShow = currentUser && currentUser.role === 'teacher'
    ? currentUser.data.materia
    : selectedSubject;

  const filtered = schedule.filter((item) => item.subject === subjectToShow);

  teacherScheduleList.innerHTML = filtered.length
    ? filtered
        .map(
          (item) => `
            <div class="schedule-item">
              <strong>${item.subject}</strong>
              <div class="schedule-meta">
                <span>${item.day}</span>
                <span>${item.time}</span>
              </div>
            </div>
          `
        )
        .join('')
    : '<div class="schedule-item"><strong>Sem aulas cadastradas</strong></div>';
}

function calculateSummary(list) {
  const { present, absent } = list.reduce(
    (summary, student) => {
      if (student.status === 'present') summary.present += 1;
      if (student.status === 'absent') summary.absent += 1;
      return summary;
    },
    { present: 0, absent: 0 }
  );
  const total = present + absent;
  const percent = Math.round((present / total) * 100);

  return { present, absent, percent: Number.isFinite(percent) ? percent : 0 };
}

function createStatusBadge(status) {
  const badge = document.createElement('span');
  badge.className = `status ${status}`;
  badge.textContent = status === 'present' ? 'Presente' : status === 'absent' ? 'Faltou' : 'Pendente';
  return badge;
}

async function renderTeacherAttendance() {
  const subject = currentUser && currentUser.role === 'teacher'
    ? currentUser.data.materia
    : teacherSubjectFilter.value;

  if (currentUser && currentUser.role === 'teacher') {
    teacherSubjectFilter.value = currentUser.data.materia;
    teacherSubjectFilter.disabled = true;
  } else {
    teacherSubjectFilter.disabled = false;
  }

  try {
    const studentsFromStorage = await apiRequest('/students');

    const attendance = studentsFromStorage.map((student) => ({
      nomeCompleto: student.nomeCompleto,
      ra: student.ra,
      status:
        student.registros && student.registros[subject] === 'presente'
          ? 'present'
          : student.registros && student.registros[subject] === 'falta'
            ? 'absent'
            : 'pending'
    }));

    const rows = attendance.map((student) => {
      const row = document.createElement('tr');
      const nameCell = document.createElement('td');
      nameCell.className = 'student-name';
      nameCell.textContent = student.nomeCompleto;

      const statusCell = document.createElement('td');
      statusCell.append(createStatusBadge(student.status));

      const actionsCell = document.createElement('td');
      const actions = document.createElement('div');
      actions.className = 'actions';
      ['present', 'absent'].forEach((status) => {
        const button = document.createElement('button');
        button.className = `status-button ${status}`;
        button.dataset.ra = student.ra;
        button.dataset.status = status;
        button.textContent = status === 'present' ? 'Presente' : 'Faltou';
        actions.append(button);
      });
      actionsCell.append(actions);
      row.append(nameCell, statusCell, actionsCell);
      return row;
    });
    studentList.replaceChildren(...rows);

    const summary = calculateSummary(attendance);
    presentCount.textContent = summary.present;
    absentCount.textContent = summary.absent;
    attendancePercent.textContent = `${summary.percent}%`;
  } catch (error) {
    alert(error.message);
  }
}

function renderStudentDashboard() {
  if (!currentUser || currentUser.role !== 'student') return;

  const student = currentUser.data;
  const history = Array.isArray(student.attendanceHistory) ? student.attendanceHistory : [];
  const summary = calculateStudentAttendance(history);
  const rows = summary.subjects.map((subjectStats) => {
    const row = document.createElement('tr');
    const subjectCell = document.createElement('td');
    subjectCell.className = 'student-subject-name';
    subjectCell.textContent = subjectStats.subject;

    const lessonsCell = document.createElement('td');
    lessonsCell.textContent = subjectStats.total;

    const absencesCell = document.createElement('td');
    absencesCell.textContent = subjectStats.absences;

    const frequencyCell = document.createElement('td');
    const frequency = document.createElement('div');
    frequency.className = 'student-frequency';
    const meter = document.createElement('div');
    meter.className = 'frequency-meter';
    meter.style.setProperty('--frequency', `${subjectStats.percentage}%`);
    meter.setAttribute('role', 'progressbar');
    meter.setAttribute('aria-label', `Frequência em ${subjectStats.subject}`);
    meter.setAttribute('aria-valuemin', '0');
    meter.setAttribute('aria-valuemax', '100');
    meter.setAttribute('aria-valuenow', String(subjectStats.percentage));
    const bar = document.createElement('span');
    bar.className = `frequency-meter-bar ${subjectStats.level}`;
    meter.append(bar);
    const percentage = document.createElement('strong');
    percentage.textContent = subjectStats.total
      ? `${formatPercentage(subjectStats.percentage)}%`
      : '—';
    frequency.append(meter, percentage);
    frequencyCell.append(frequency);

    const remainingCell = document.createElement('td');
    remainingCell.textContent = subjectStats.total ? subjectStats.remaining : '—';

    const statusCell = document.createElement('td');
    const statusBadge = document.createElement('span');
    statusBadge.className = `student-attendance-status ${subjectStats.level}`;
    statusBadge.textContent = subjectStats.status;
    statusCell.append(statusBadge);
    row.append(subjectCell, lessonsCell, absencesCell, frequencyCell, remainingCell, statusCell);
    return row;
  });
  studentRecordsTable.replaceChildren(...rows);

  studentNameDisplay.textContent = student.nomeCompleto;
  studentRaDisplay.textContent = student.ra;
  studentUpdatedDate.textContent = new Date().toLocaleDateString('pt-BR');
  studentPresentCount.textContent = summary.present;
  studentAbsentCount.textContent = summary.absences;
  studentAttendancePercent.textContent = summary.total
    ? `${formatPercentage(summary.percentage)}%`
    : '—';
  studentLessonsCount.textContent = `de ${summary.total} ${summary.total === 1 ? 'aula dada' : 'aulas dadas'}`;
  studentAttendanceAlert.className = summary.total
    ? `student-attendance-alert ${summary.warningCount ? 'warning' : 'healthy'}`
    : 'student-attendance-alert';
  studentAttendanceAlert.textContent = summary.total
    ? summary.warningCount
      ? `${summary.warningCount} ${summary.warningCount === 1 ? 'matéria no limite ou abaixo' : 'matérias no limite ou abaixo'} do mínimo.`
      : 'Sua frequência está acima do mínimo exigido.'
    : 'Ainda não há chamadas registradas para esta conta.';

  const recentAbsences = history
    .filter((record) => record.status === 'falta' && record.date)
    .sort((a, b) => {
      const aTime = a.recordedAt ? Date.parse(a.recordedAt) : Date.parse(`${a.date}T00:00:00`);
      const bTime = b.recordedAt ? Date.parse(b.recordedAt) : Date.parse(`${b.date}T00:00:00`);
      return bTime - aTime;
    })
    .slice(0, 5);
  if (!recentAbsences.length) {
    const emptyMessage = document.createElement('li');
    emptyMessage.className = 'student-recent-empty';
    emptyMessage.textContent = summary.total
      ? 'Nenhuma falta registrada.'
      : 'As faltas registradas aparecerão aqui.';
    studentRecentAbsences.replaceChildren(emptyMessage);
    return;
  }

  const recentRows = recentAbsences.map((record) => {
    const item = document.createElement('li');
    const details = document.createElement('div');
    const subject = document.createElement('strong');
    subject.textContent = record.subject;
    const scheduleItem = schedule.find((entry) => entry.subject === record.subject);
    const recordDate = new Date(`${record.date}T00:00:00`);
    const dateTime = document.createElement('small');
    const weekday = recordDate.toLocaleDateString('pt-BR', { weekday: 'long' });
    dateTime.textContent = `${weekday[0].toLocaleUpperCase()}${weekday.slice(1)}${scheduleItem ? ` · ${scheduleItem.time}` : ''} · ${recordDate.toLocaleDateString('pt-BR')}`;
    details.append(subject, dateTime);
    const date = document.createElement('time');
    date.dateTime = record.date;
    date.textContent = recordDate.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit'
    });
    item.append(details, date);
    return item;
  });
  studentRecentAbsences.replaceChildren(...recentRows);
}

function formatPercentage(value) {
  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: value % 1 ? 1 : 0,
    maximumFractionDigits: 1
  }).format(value);
}

function calculateStudentAttendance(history) {
  const subjectStats = studentDashboardSubjects.map((subject) => {
    const records = history.filter((record) =>
      record.subject === subject && ['presente', 'falta'].includes(record.status)
    );
    const present = records.filter((record) => record.status === 'presente').length;
    const absences = records.filter((record) => record.status === 'falta').length;
    const total = present + absences;
    const percentage = total ? (present / total) * 100 : 0;
    const maximumAbsences = Math.floor(total * 0.25);
    const level = !total
      ? 'no-data'
      : absences > maximumAbsences
        ? 'below'
        : absences === maximumAbsences
          ? 'at-limit'
          : 'on-track';

    return {
      subject,
      present,
      absences,
      total,
      percentage,
      remaining: Math.max(0, maximumAbsences - absences),
      level,
      status: !total
        ? 'Sem registros'
        : level === 'below'
          ? 'Abaixo do mínimo'
          : level === 'at-limit'
            ? 'No limite'
            : 'Em dia'
    };
  });
  const total = subjectStats.reduce((sum, subject) => sum + subject.total, 0);
  const present = subjectStats.reduce((sum, subject) => sum + subject.present, 0);
  const absences = subjectStats.reduce((sum, subject) => sum + subject.absences, 0);

  return {
    subjects: subjectStats,
    total,
    present,
    absences,
    percentage: total ? (present / total) * 100 : 0,
    warningCount: subjectStats.filter((subject) =>
      subject.total && subject.percentage <= 75
    ).length
  };
}

function normalizeText(value) {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

function answerAttendanceQuestion(question) {
  if (!currentUser || currentUser.role !== 'student') {
    return 'Entre na sua conta de aluno para consultar os registros de frequência.';
  }

  const history = Array.isArray(currentUser.data.attendanceHistory)
    ? currentUser.data.attendanceHistory
    : [];
  const summary = calculateStudentAttendance(history);
  const normalizedQuestion = normalizeText(question);

  const matchingSubject = summary.subjects.find(({ subject }) =>
    normalizedQuestion.includes(normalizeText(subject))
  );

  if (matchingSubject && /falt|ausen|reprov|limite/.test(normalizedQuestion)) {
    const { subject, total, absences, remaining, level } = matchingSubject;
    if (!total) {
      return `Ainda não há chamadas registradas para ${subject}.`;
    }
    return level === 'below'
      ? `Em ${subject}, você tem ${absences} falta(s) em ${total} aula(s) e está abaixo do mínimo de 75%.`
      : `Em ${subject}, você tem ${absences} falta(s) em ${total} aula(s). Pela regra de 75%, ainda pode faltar ${remaining} vez(es) sem ficar abaixo do mínimo.`;
  }

  if (!matchingSubject && /reprov|reprova|limite de falt|frequencia minima/.test(normalizedQuestion)) {
    if (!summary.total) {
      return 'A regra geral costuma exigir 75% de frequência, mas ainda não há chamadas registradas para calcular sua situação.';
    }
    return `A regra geral usada aqui é de 75% de frequência. Até agora, você tem ${summary.absences} falta(s) em ${summary.total} aula(s), com ${formatPercentage(summary.percentage)}% de frequência. Confira também as regras específicas da sua escola.`;
  }

  if (matchingSubject) {
    const { subject, total, absences, percentage, remaining, level } = matchingSubject;
    if (!total) {
      return `Ainda não há chamadas registradas para ${subject}.`;
    }
    if (/frequ|presenc/.test(normalizedQuestion)) {
      return `Em ${subject}, sua frequência é ${formatPercentage(percentage)}%: ${total - absences} presença(s) em ${total} aula(s), com ${absences} falta(s).`;
    }
    if (/falt|ausen/.test(normalizedQuestion)) {
      return level === 'below'
        ? `Em ${subject}, você tem ${absences} falta(s) em ${total} aula(s) e está abaixo do mínimo de 75%.`
        : `Em ${subject}, você tem ${absences} falta(s) em ${total} aula(s). Pela regra de 75%, ainda pode faltar ${remaining} vez(es) sem ficar abaixo do mínimo.`;
    }
  }

  if (/falt|ausen/.test(normalizedQuestion)) {
    if (!summary.total) {
      return 'Ainda não há registros de frequência disponíveis para esta conta.';
    }

    const subjectsWithAbsences = summary.subjects
      .filter((subject) => subject.absences)
      .map((subject) => `${subject.subject} (${subject.absences})`);
    const subjectList = subjectsWithAbsences.length
      ? ` Por matéria: ${subjectsWithAbsences.join(', ')}.`
      : ' Você não tem faltas registradas.';
    return `Você tem ${summary.absences} falta(s) em ${summary.total} aula(s) registrada(s).${subjectList}`;
  }

  if (/frequ|presenc|presencas/.test(normalizedQuestion)) {
    if (!summary.total) {
      return 'Ainda não há registros de frequência disponíveis para calcular a porcentagem.';
    }

    return `Sua frequência geral é de ${formatPercentage(summary.percentage)}%: ${summary.present} presença(s) em ${summary.total} aula(s) registrada(s).`;
  }

  return 'Posso consultar suas faltas, frequência geral ou situação por matéria. Por exemplo: “Quantas faltas ainda posso ter em Matemática?”';
}

function appendAssistantMessage(message, className) {
  const messageElement = document.createElement('p');
  messageElement.className = className;
  messageElement.textContent = message;
  assistantConversation.append(messageElement);
  messageElement.scrollIntoView({ block: 'nearest' });
}

function resetAssistantConversation() {
  assistantConversation.replaceChildren();
  appendAssistantMessage('Olá! Posso consultar suas presenças, faltas e frequência mínima.', 'assistant-message');
}

function handleAttendanceAssistantQuestion(event) {
  event.preventDefault();
  const question = attendanceAssistantQuestion.value.trim();
  if (!question) return;

  appendAssistantMessage(question, 'user-message');
  appendAssistantMessage(answerAttendanceQuestion(question), 'assistant-message');
  attendanceAssistantForm.reset();
  attendanceAssistantQuestion.focus();
}

async function handleTeacherRegister(event) {
  event.preventDefault();

  const nomeCompleto = document.getElementById('teacherFullName').value.trim();
  const cpf = document.getElementById('teacherCpf').value.trim();
  const materia = document.getElementById('teacherSubject').value;
  const senha = document.getElementById('teacherPassword').value.trim();

  if (!nomeCompleto || !cpf || !senha || !materia) {
    alert('Preencha todos os campos do professor.');
    return;
  }

  try {
    await apiRequest('/teachers/register', {
      method: 'POST',
      body: JSON.stringify({ nomeCompleto, cpf, materia, senha })
    });

    teacherRegisterForm.reset();
    alert('Professor cadastrado com sucesso! Agora faça o login.');
    showScreen('teacherLoginScreen');
  } catch (error) {
    alert(error.message);
  }
}

async function handleTeacherLogin(event) {
  event.preventDefault();

  const cpf = document.getElementById('loginTeacherCpf').value.trim();
  const senha = document.getElementById('loginTeacherPassword').value.trim();

  try {
    const response = await apiRequest('/teachers/login', {
      method: 'POST',
      body: JSON.stringify({ cpf, senha })
    });

    currentUser = { role: 'teacher', data: response.teacher };
    setUserBadge(`Professor(a): ${response.teacher.nomeCompleto}`);
    teacherTitle.textContent = `Chamada da turma - ${response.teacher.materia}`;
    teacherSubjectFilter.value = response.teacher.materia;
    teacherSubjectFilter.disabled = true;
    renderTeacherSchedule(response.teacher.materia);
    await renderTeacherAttendance();
    showScreen('teacherDashboard');
    teacherLoginForm.reset();
  } catch (error) {
    alert(error.message);
  }
}

async function createStudentAccount(nomeCompleto, ra, senha) {
  try {
    const response = await apiRequest('/students/register', {
      method: 'POST',
      body: JSON.stringify({ nomeCompleto, ra, senha })
    });

    return { ok: true, message: 'Aluno cadastrado com sucesso.', student: response.student };
  } catch (error) {
    return { ok: false, message: error.message };
  }
}

async function handleStudentRegister(event) {
  event.preventDefault();

  const nomeCompleto = document.getElementById('studentFullName').value.trim();
  const ra = document.getElementById('studentRa').value.trim();
  const senha = document.getElementById('studentPassword').value.trim();

  if (!nomeCompleto || !ra || !senha) {
    alert('Preencha todos os campos do aluno.');
    return;
  }

  const result = await createStudentAccount(nomeCompleto, ra, senha);

  if (!result.ok) {
    alert(result.message);
    return;
  }

  studentRegisterForm.reset();
  alert('Aluno cadastrado com sucesso! Agora faça o login.');
  showScreen('studentLoginScreen');
}

async function handleStudentLogin(event) {
  event.preventDefault();

  const ra = document.getElementById('loginStudentRa').value.trim();
  const senha = document.getElementById('loginStudentPassword').value.trim();

  try {
    const response = await apiRequest('/students/login', {
      method: 'POST',
      body: JSON.stringify({ ra, senha })
    });

    currentUser = { role: 'student', data: response.student };
    setUserBadge(`Aluno: ${response.student.nomeCompleto}`);
    resetAssistantConversation();
    renderStudentDashboard();
    showScreen('studentDashboard');
    studentLoginForm.reset();
  } catch (error) {
    alert(error.message);
  }
}

async function resetAttendance() {
  const subject = currentUser && currentUser.role === 'teacher'
    ? currentUser.data.materia
    : teacherSubjectFilter.value;

  try {
    await apiRequest('/attendance/reset', {
      method: 'POST',
      body: JSON.stringify({ subject })
    });

    await renderTeacherAttendance();
  } catch (error) {
    alert(error.message);
  }
}

async function handleTeacherStudentAction(event) {
  const button = event.target.closest('button[data-ra]');
  if (!button) return;

  const studentRa = button.dataset.ra;
  const status = button.dataset.status;
  const subject = currentUser && currentUser.role === 'teacher'
    ? currentUser.data.materia
    : teacherSubjectFilter.value;

  try {
    await apiRequest('/attendance', {
      method: 'PUT',
      body: JSON.stringify({ studentRa, subject, status })
    });

    await renderTeacherAttendance();
  } catch (error) {
    alert(error.message);
  }
}

function logoutCurrentUser() {
  currentUser = null;
  setUserBadge('Acesso não iniciado');
  resetAssistantConversation();
  showScreen('accessScreen');
}

teacherAccessBtn.addEventListener('click', () => showScreen('teacherLoginScreen'));
studentAccessBtn.addEventListener('click', () => showScreen('studentLoginScreen'));
teacherRegisterHomeBtn.addEventListener('click', () => showScreen('teacherRegisterScreen'));
studentRegisterHomeBtn.addEventListener('click', () => showScreen('studentRegisterScreen'));
teacherNavBtn.addEventListener('click', () => showScreen('teacherChoiceScreen'));
studentNavBtn.addEventListener('click', () => showScreen('studentChoiceScreen'));

teacherRegisterChoiceBtn.addEventListener('click', () => showScreen('teacherRegisterScreen'));
teacherLoginChoiceBtn.addEventListener('click', () => showScreen('teacherLoginScreen'));
studentRegisterChoiceBtn.addEventListener('click', () => showScreen('studentRegisterScreen'));
studentLoginChoiceBtn.addEventListener('click', () => showScreen('studentLoginScreen'));
backTeacherToAccess.addEventListener('click', () => showScreen('accessScreen'));
backStudentToAccess.addEventListener('click', () => showScreen('accessScreen'));

backTeacherFromRegister.addEventListener('click', () => showScreen('teacherChoiceScreen'));
backTeacherFromLogin.addEventListener('click', () => showScreen('teacherChoiceScreen'));
backStudentFromRegister.addEventListener('click', () => showScreen('studentChoiceScreen'));
backStudentFromLogin.addEventListener('click', () => showScreen('studentChoiceScreen'));

teacherRegisterForm.addEventListener('submit', handleTeacherRegister);
teacherLoginForm.addEventListener('submit', handleTeacherLogin);
studentRegisterForm.addEventListener('submit', handleStudentRegister);
studentLoginForm.addEventListener('submit', handleStudentLogin);

studentList.addEventListener('click', handleTeacherStudentAction);
resetBtn.addEventListener('click', resetAttendance);
teacherSubjectFilter.addEventListener('change', async () => {
  if (currentUser && currentUser.role === 'teacher') {
    teacherTitle.textContent = `Chamada da turma - ${currentUser.data.materia}`;
    renderTeacherSchedule(currentUser.data.materia);
    await renderTeacherAttendance();
    return;
  }

  teacherTitle.textContent = `Chamada da turma - ${teacherSubjectFilter.value}`;
  renderTeacherSchedule(teacherSubjectFilter.value);
  await renderTeacherAttendance();
});

document.getElementById('teacherLogoutBtn').addEventListener('click', logoutCurrentUser);
document.getElementById('studentLogoutBtn').addEventListener('click', logoutCurrentUser);
attendanceAssistantForm.addEventListener('submit', handleAttendanceAssistantQuestion);

showScreen('accessScreen');
renderTeacherSchedule();
