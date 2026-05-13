package editarDados;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import Trrabalhor.Sistema;
import telaDados.DadosUsuario;

public class telaeditarDados extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField fNome, fCpf, fEmail, fCidade, fRua;
	private JFormattedTextField fNasc, fTel;

	public telaeditarDados(Sistema usuarioLogado) {
		setTitle("SAVA - Editando Meus Dados");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 720);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		JPanel topo = new JPanel();
		topo.setBackground(new Color(0, 84, 168));
		topo.setBounds(0, 0, 400, 53);
		contentPane.add(topo);
		JLabel lblTit = new JLabel("Editando Meus Dados");
		lblTit.setHorizontalAlignment(SwingConstants.CENTER);
		lblTit.setForeground(Color.WHITE);
		lblTit.setFont(new Font("Tahoma", Font.BOLD, 18));
		topo.add(lblTit);

		fNome = criarCampo("Nome Completo (Bloqueado):", 70, usuarioLogado.getNome(), false);
		fCpf = criarCampo("CPF (Bloqueado):", 130, usuarioLogado.getCpf(), false);
		fEmail = criarCampo("E-mail (Bloqueado):", 190, usuarioLogado.getEmail(), false);

		// --- DATA DE NASCIMENTO ---
		JLabel lblNasc = new JLabel("Data de Nascimento:");
		lblNasc.setBounds(40, 270, 300, 20);
		contentPane.add(lblNasc);
		try {
			MaskFormatter mascaraData = new MaskFormatter("##/##/####");
			mascaraData.setPlaceholderCharacter('_');
			fNasc = new JFormattedTextField(mascaraData);
		
			fNasc.setValue(usuarioLogado.getNascimento()); 
		} catch (ParseException e) {
			fNasc = new JFormattedTextField();
		}
		fNasc.setBounds(40, 290, 310, 30);
		contentPane.add(fNasc);

		fCidade = criarCampo("Cidade:", 330, usuarioLogado.getCidade(), true);
		fRua = criarCampo("Rua:", 390, usuarioLogado.getRua(), true);

		// --- TELEFONE ---
		JLabel lblTel = new JLabel("Telefone:");
		lblTel.setBounds(40, 450, 300, 20);
		contentPane.add(lblTel);
		try {
			MaskFormatter mascaraTel = new MaskFormatter("(##) #####-####");
			mascaraTel.setPlaceholderCharacter('_');
			//  getValue() para retornar apenas os números, sem parênteses e traço
			mascaraTel.setValueContainsLiteralCharacters(false); 
			fTel = new JFormattedTextField(mascaraTel);
			fTel.setValue(usuarioLogado.getTelefone());
		} catch (ParseException e) {
			fTel = new JFormattedTextField();
		}
		fTel.setBounds(40, 470, 310, 30);
		contentPane.add(fTel);

		// --- BOTÃO SALVAR ---
		JButton btnSalvar = new JButton("SALVAR ALTERAÇÕES");
		btnSalvar.setBackground(new Color(0, 84, 168));
		btnSalvar.setForeground(Color.WHITE);
		btnSalvar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSalvar.setBounds(40, 540, 310, 45);
		contentPane.add(btnSalvar);

		JButton btnVoltar = new JButton("CANCELAR / VOLTAR");
		btnVoltar.setBounds(40, 595, 310, 35);
		contentPane.add(btnVoltar);

		btnSalvar.addActionListener(e -> {
			// AJUSTE NO SALVAMENTO:
			
			usuarioLogado.setNascimento(fNasc.getText());
			usuarioLogado.setCidade(fCidade.getText());
			usuarioLogado.setRua(fRua.getText());
			
		
			String telLimpo = fTel.getText().replaceAll("[^0-9]", "");
			usuarioLogado.setTelefone(telLimpo);
			
			JOptionPane.showMessageDialog(null, "Dados salvos com sucesso!");
			new DadosUsuario(usuarioLogado).setVisible(true);
			dispose();
		});

		btnVoltar.addActionListener(e -> {
			new DadosUsuario(usuarioLogado).setVisible(true);
			dispose();
		});
	}

	private JTextField criarCampo(String label, int y, String valor, boolean editavel) {
		JLabel l = new JLabel(label);
		l.setBounds(40, y, 300, 20);
		contentPane.add(l);
		JTextField t = new JTextField(valor);
		t.setBounds(40, y + 20, 310, 30);
		t.setEditable(editavel);
		if (!editavel) t.setBackground(new Color(230, 230, 230));
		contentPane.add(t);
		return t;
	}
}