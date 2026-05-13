package Trrabalhor;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;

public class TelaCadastro extends JFrame {

    private JTextField campoNome, campoEmail;
    private JPasswordField campoSenha, campoConfirmarSenha;
    private JFormattedTextField campoCpf;
    private JButton botaoCadastrar, botaoVoltar;
    private JLabel LoginNome;
    private JLabel CadastroCpf;
    private JLabel CadastroEmail;
    private JLabel CadastroSenha;
    private JLabel cadastroSenha;

    public TelaCadastro() {
        setTitle("SAVA - Cadastro");
        setSize(320, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Apenas fecha esta janela
        setLocationRelativeTo(null);

        JPanel painel = new JPanel();
        painel.setLayout(null);
        painel.setBackground(new Color(240, 240, 240));

        // Título Azul
        JLabel lblTitulo = new JLabel("Cadastro", SwingConstants.CENTER);
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(0, 87, 174));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(0, 0, 320, 40);
        painel.add(lblTitulo);

        
        int yIncial = 60;
        int espacamento = 60;

        // Nome
        criarLabel("Nome completo:", yIncial, painel);
        campoNome = new JTextField();
        campoNome.setBounds(45, yIncial + 20, 210, 30);
        painel.add(campoNome);

        // CPF com Máscara
        criarLabel("CPF:", yIncial + espacamento, painel);
        try {
            MaskFormatter mf = new MaskFormatter("###.###.###-##");
            mf.setPlaceholderCharacter('_');
            campoCpf = new JFormattedTextField(mf);
        } catch (Exception e) { campoCpf = new JFormattedTextField(); }
        campoCpf.setBounds(45, yIncial + espacamento + 20, 210, 30);
        painel.add(campoCpf);

        // Email
        criarLabel("E-mail:", yIncial + (espacamento * 2), painel);
        campoEmail = new JTextField();
        campoEmail.setBounds(45, yIncial + (espacamento * 2) + 20, 210, 30);
        painel.add(campoEmail);

        // Senha
        criarLabel("Senha:", yIncial + (espacamento * 3), painel);
        campoSenha = new JPasswordField();
        campoSenha.setBounds(45, yIncial + (espacamento * 3) + 20, 210, 30);
        painel.add(campoSenha);

        // Confirmar Senha
        criarLabel("Confirmar Senha:", yIncial + (espacamento * 4), painel);
        campoConfirmarSenha = new JPasswordField();
        campoConfirmarSenha.setBounds(45, yIncial + (espacamento * 4) + 20, 210, 30);
        painel.add(campoConfirmarSenha);

        // Botão Cadastrar
        botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.setBackground(new Color(0, 87, 174));
        botaoCadastrar.setForeground(Color.WHITE);
        botaoCadastrar.setBounds(45, 400, 210, 35);
        painel.add(botaoCadastrar);

        // Ação do Botão
        botaoCadastrar.addActionListener(e -> acaoCadastrar());

        getContentPane().add(painel);
        
        LoginNome = new JLabel("");
        LoginNome.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\user.cadastro.png"));
        LoginNome.setBounds(21, 88, 24, 22);
        painel.add(LoginNome);
        
        CadastroCpf = new JLabel("");
        CadastroCpf.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\credit-card.png"));
        CadastroCpf.setBounds(21, 150, 24, 20);
        painel.add(CadastroCpf);
        
        CadastroEmail = new JLabel("");
        CadastroEmail.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\email (2).png"));
        CadastroEmail.setBounds(21, 200, 24, 40);
        painel.add(CadastroEmail);
        
        CadastroSenha = new JLabel("");
        CadastroSenha.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\padlock (1).png"));
        CadastroSenha.setBounds(21, 268, 24, 22);
        painel.add(CadastroSenha);
        
        cadastroSenha = new JLabel("");
        cadastroSenha.setIcon(new ImageIcon("C:\\Users\\SPEBR3935\\Downloads\\padlock (1).png"));
        cadastroSenha.setBounds(21, 328, 24, 22);
        painel.add(cadastroSenha);
    }

    private void criarLabel(String texto, int y, JPanel p) {
        JLabel l = new JLabel(texto);
        l.setBounds(45, y, 150, 20);
        p.add(l);
    }

    private void acaoCadastrar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String confirma = new String(campoConfirmarSenha.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.contains("_")) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }

        if (!senha.equals(confirma)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem!");
            return;
        }

        // Salva na lista bloqueada
        Sistema novo = new Sistema(nome, email, cpf, senha);
        Sistema.usuariosCadastrados.add(novo);

        JOptionPane.showMessageDialog(this, "Usuário " + nome + " cadastrado!");
        this.dispose(); // Fecha e volta para o login
    }
}