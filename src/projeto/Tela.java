package projeto;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.ParseException;

// Importação dos outros pacotes do seu projeto
import Trrabalhor.Sistema; 
import Trrabalhor.TelaCadastro;
import telaPrincipal.TelaInicial;

public class Tela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField campoEmail;
	private JPasswordField campoSenha;
	private JFormattedTextField campoCpf;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tela frame = new Tela();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Tela() {
		setTitle("SAVA - Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 362, 510);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null); // Centraliza a tela
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 255, 255));
		panel.setBounds(0, 0, 358, 476);
		contentPane.add(panel);
		panel.setLayout(null);
		
		// --- CONFIGURAÇÃO DO CPF ---
		try {
			MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
			mascaraCpf.setPlaceholderCharacter('_');
			campoCpf = new JFormattedTextField(mascaraCpf);
		} catch (ParseException e) {
			campoCpf = new JFormattedTextField();
		}

		JLabel lbldgitarCpf = new JLabel("Digite seu CPF:");
		lbldgitarCpf.setHorizontalAlignment(SwingConstants.CENTER);
		lbldgitarCpf.setBounds(72, 168, 213, 22);
		panel.add(lbldgitarCpf);
		
		campoCpf.setBounds(72, 193, 213, 31);
		panel.add(campoCpf);
		
		JLabel lbldigiteEmail = new JLabel("Digite seu Email:");
		lbldigiteEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lbldigiteEmail.setBounds(72, 230, 213, 22);
		panel.add(lbldigiteEmail);
		
		campoEmail = new JTextField();
		campoEmail.setBounds(72, 253, 213, 31);
		panel.add(campoEmail);
		
		JLabel lbldigitarsenha = new JLabel("Digite sua senha:");
		lbldigitarsenha.setHorizontalAlignment(SwingConstants.CENTER);
		lbldigitarsenha.setBounds(72, 285, 213, 22);
		panel.add(lbldigitarsenha);
		
		campoSenha = new JPasswordField();
		campoSenha.setBounds(72, 306, 213, 31);
		panel.add(campoSenha);

		// --- BOTÃO ENTRAR ---
		JButton btnBotaoEntrar = new JButton("Entrar");
		btnBotaoEntrar.setForeground(Color.WHITE);
		btnBotaoEntrar.setBackground(new Color(0, 98, 196));
		btnBotaoEntrar.setBounds(108, 359, 118, 35);
		btnBotaoEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cpfDigitado = campoCpf.getText();
				String senhaDigitada = new String(campoSenha.getPassword());

				Sistema usuarioLogado = null;

				// Busca o usuário na lista da classe Sistema
				for (Sistema u : Sistema.usuariosCadastrados) {
					if (u.getCpf().equals(cpfDigitado)) {
						usuarioLogado = u;
						break;
					}
				}

				if (usuarioLogado == null) {
					JOptionPane.showMessageDialog(null, "Usuário não encontrado! Realize o cadastro.", "Erro", JOptionPane.ERROR_MESSAGE);
				} else if (!usuarioLogado.getSenha().equals(senhaDigitada)) {
					JOptionPane.showMessageDialog(null, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Bem-vindo, " + usuarioLogado.getNome() + "!");
					
					// REDIRECIONA PARA A TELA INICIAL (MENU DE BOTÕES)
					TelaInicial menu = new TelaInicial(usuarioLogado);
					menu.setVisible(true);
					dispose(); // Fecha a tela de login
				}
			}
		});
		panel.add(btnBotaoEntrar);
		
		// --- BOTÃO CADASTRAR ---
		JButton btnCadastrar = new JButton("Cadastrar");
		btnCadastrar.setBounds(108, 423, 118, 22);
		btnCadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TelaCadastro telaCad = new TelaCadastro();
				telaCad.setVisible(true);
			}
		});
		panel.add(btnCadastrar);
		
		JLabel LogoLogins = new JLabel("");
		LogoLogins.setIcon(new ImageIcon(Tela.class.getResource("/images/logo_100x100.png")));
		LogoLogins.setBounds(119, 51, 107, 96);
		panel.add(LogoLogins);
		
		JLabel LoginCpf = new JLabel("");
		LoginCpf.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\browser.png"));
		LoginCpf.setBounds(45, 193, 31, 36);
		panel.add(LoginCpf);
		
		JLabel LoginEmail = new JLabel("");
		LoginEmail.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\user.png"));
		LoginEmail.setBounds(45, 256, 31, 28);
		panel.add(LoginEmail);
		
		JLabel Loginsenha = new JLabel("");
		Loginsenha.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\padlock.png"));
		Loginsenha.setBounds(45, 306, 31, 31);
		panel.add(Loginsenha);
		
		JLabel lblLogin = new JLabel("Login", SwingConstants.CENTER);
		lblLogin.setOpaque(true);
		lblLogin.setForeground(Color.WHITE);
		lblLogin.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblLogin.setBackground(new Color(0, 87, 174));
		lblLogin.setBounds(0, 0, 348, 40);
		panel.add(lblLogin);
	}
}