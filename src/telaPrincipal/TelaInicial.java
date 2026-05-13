package telaPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Trrabalhor.Sistema; 
import TelaEspecialidade.Tela3;
import telaDados.DadosUsuario;
import verificarAgendamentos.TelaMeusAgendamentos;

public class TelaInicial extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public TelaInicial(Sistema usuarioLogado) {
        setTitle("SAVA - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 385, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // --- CABEÇALHO AZUL ---
        JLabel lblTitulo = new JLabel("Clínica Fácil", SwingConstants.CENTER);
        lblTitulo.setOpaque(true);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBackground(new Color(0, 87, 174));
        lblTitulo.setBounds(0, 0, 371, 45);
        contentPane.add(lblTitulo);

        // --- PAINEL DE BOAS-VINDAS ---
        JPanel topo = new JPanel();
        topo.setBackground(Color.WHITE);
        topo.setBounds(10, 55, 350, 80);
        topo.setLayout(null);
        contentPane.add(topo);

        JLabel lblBemVindo = new JLabel("Olá, " + usuarioLogado.getNome());
        lblBemVindo.setForeground(new Color(0, 64, 128));
        lblBemVindo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblBemVindo.setBounds(15, 15, 250, 25);
        topo.add(lblBemVindo);

        JLabel lblSubtexto = new JLabel("Seja Bem-Vindo(a)");
        lblSubtexto.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSubtexto.setBounds(15, 45, 150, 15);
        topo.add(lblSubtexto);

        // Ícone de Perfil no topo
        JLabel lblUserIcon = new JLabel(carregarIcone("/images/user (2).png", 50, 50));
        lblUserIcon.setBounds(280, 15, 50, 50);
        topo.add(lblUserIcon);

        // --- BOTÕES CARD (DADOS, AGENDAR, MEUS AGENDAMENTOS) ---

        // Dados Pessoais
        JButton btnDados = criarBotaoCard("Dados Pessoais", "Visualize e edite seus dados", "/images/user (3).png", 150);
        contentPane.add(btnDados);
        btnDados.addActionListener(e -> {
            new DadosUsuario(usuarioLogado).setVisible(true);
            dispose();
        });

        // Agendar Consulta
        JButton btnAgendar = criarBotaoCard("Agendar Nova Consulta", "Agende suas consultas", "/images/plus.png", 245);
        contentPane.add(btnAgendar);
        btnAgendar.addActionListener(e -> {
            new Tela3(usuarioLogado).setVisible(true);
            dispose();
        });

        // Meus Agendamentos
        JButton btnAgendamentos = criarBotaoCard("Meus Agendamentos", "Verifique ou cancele suas consultas", "/images/calendar (1).png", 340);
        contentPane.add(btnAgendamentos);
        btnAgendamentos.addActionListener(e -> {
            new TelaMeusAgendamentos(usuarioLogado).setVisible(true);
            dispose();
        });

        // --- BOTÃO SAIR ---
        JButton btnSair = new JButton("Sair do Sistema");
        btnSair.setForeground(new Color(204, 0, 0));
        btnSair.setBackground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setBounds(110, 500, 150, 35);
        contentPane.add(btnSair);
        btnSair.addActionListener(e -> System.exit(0));
    }

    /**
     * Método para criar os botões com ícones 
     */
    private JButton criarBotaoCard(String titulo, String subtitulo, String caminhoIcone, int y) {
        JButton btn = new JButton();
        btn.setLayout(null);
        btn.setBounds(15, y, 340, 85);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        
        JLabel lblIcon = new JLabel(carregarIcone(caminhoIcone, 50, 50));
        lblIcon.setBounds(15, 15, 55, 55);
        btn.add(lblIcon);

        
        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTitle.setBounds(85, 20, 240, 20);
        btn.add(lblTitle);

    
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(85, 45, 240, 20);
        btn.add(lblSub);

        return btn;
    }

    
    private ImageIcon carregarIcone(String caminho, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(caminho);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                System.err.println("Imagem não encontrada no pacote: " + caminho);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}