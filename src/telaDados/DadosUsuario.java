package telaDados;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import Trrabalhor.Sistema;
import telaPrincipal.TelaInicial;
import editarDados.telaeditarDados;

public class DadosUsuario extends JFrame {
    private JPanel contentPane;
    private JFormattedTextField fNasc, fTel; 
    private JTextField fNome, fCpf, fEmail, fCidade, fRua;

    public DadosUsuario(Sistema usuarioLogado) {
        setTitle("SAVA - Meus Dados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 700); 
        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- BARRA AZUL SUPERIOR (MAIS FINA) ---
        JPanel topo = new JPanel();
        topo.setBackground(new Color(0, 87, 174));
        topo.setBounds(0, 0, 400, 45);
        contentPane.add(topo);
        JLabel lblTit = new JLabel("Dados Pessoais");
        lblTit.setForeground(Color.WHITE);
        lblTit.setFont(new Font("Tahoma", Font.BOLD, 17));
        topo.add(lblTit);

        // --- CARD DE PERFIL  ---
        JPanel cardPerfil = new JPanel();
        cardPerfil.setBackground(Color.WHITE);
        cardPerfil.setBounds(45, 55, 300, 100); 
        cardPerfil.setLayout(null);
        cardPerfil.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        contentPane.add(cardPerfil);

        JLabel lblIcon = new JLabel(carregarIcone("/images/user (3).png", 50, 50));
        lblIcon.setBounds(125, 5, 50, 50); 
        cardPerfil.add(lblIcon);

        JLabel lblNomeUsuario = new JLabel(usuarioLogado.getNome(), SwingConstants.CENTER);
        lblNomeUsuario.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNomeUsuario.setBounds(0, 60, 300, 20);
        cardPerfil.add(lblNomeUsuario);

        JLabel lblTipo = new JLabel("Paciente");
        lblTipo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTipo.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblTipo.setForeground(Color.GRAY);
        lblTipo.setBounds(0, 78, 300, 15);
        cardPerfil.add(lblTipo);

        
        fNome   = addCampo("Nome Completo:", 165, usuarioLogado.getNome());
        fCpf    = addCampo("CPF:", 213, usuarioLogado.getCpf());
        fEmail  = addCampo("E-mail:", 261, usuarioLogado.getEmail());
        
        // Data com Máscara (12/12/1234)
        fNasc = addCampoFormatado("Data de Nascimento:", 309, "##/##/####", usuarioLogado.getNascimento());
        
        fCidade = addCampo("Cidade:", 357, usuarioLogado.getCidade());
        fRua    = addCampo("Rua:", 405, usuarioLogado.getRua());
        
        // Telefone com Máscara ((##) ####-####)
        fTel = addCampoFormatado("Telefone:", 453, "(##) ####-####", usuarioLogado.getTelefone());

        // --- BOTÕES ---
        JButton btnEditar = new JButton("ADICIONAR / EDITAR MEUS DADOS");
        btnEditar.setBackground(new Color(0, 87, 174));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnEditar.setBounds(45, 550, 300, 38);
        btnEditar.setFocusPainted(false);
        contentPane.add(btnEditar);

        JButton btnVoltar = new JButton("VOLTAR AO MENU");
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.setBounds(45, 595, 300, 32);
        btnVoltar.setFocusPainted(false);
        contentPane.add(btnVoltar);

        // --- AÇÕES ---
        btnEditar.addActionListener(e -> {
            new telaeditarDados(usuarioLogado).setVisible(true);
            dispose();
        });

        btnVoltar.addActionListener(e -> {
            new TelaInicial(usuarioLogado).setVisible(true);
            dispose();
        });
    }

    // Método para campos normais
    private JTextField addCampo(String label, int y, String valor) {
        JLabel l = new JLabel(label);
        l.setBounds(45, y, 200, 15);
        l.setFont(new Font("Tahoma", Font.BOLD, 10));
        contentPane.add(l);
        
        JTextField t = new JTextField(valor);
        t.setBounds(45, y + 16, 300, 26);
        t.setEditable(false);
        t.setBackground(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        contentPane.add(t);
        return t;
    }

    //  máscara para Data e Telefone
    private JFormattedTextField addCampoFormatado(String label, int y, String mascara, String valor) {
        JLabel l = new JLabel(label);
        l.setBounds(45, y, 200, 15);
        l.setFont(new Font("Tahoma", Font.BOLD, 10));
        contentPane.add(l);

        JFormattedTextField ft = null;
        try {
            MaskFormatter mf = new MaskFormatter(mascara);
            mf.setPlaceholderCharacter('_'); // Deixa o tracinho onde não tem número
            ft = new JFormattedTextField(mf);
            ft.setText(valor);
        } catch (ParseException e) {
            ft = new JFormattedTextField();
        }

        ft.setBounds(45, y + 16, 300, 26);
        ft.setEditable(false);
        ft.setBackground(Color.WHITE);
        ft.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        contentPane.add(ft);
        return ft;
    }

    private ImageIcon carregarIcone(String caminho, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(caminho);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {}
        return null;
    }
}