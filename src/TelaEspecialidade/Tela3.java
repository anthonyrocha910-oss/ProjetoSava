package TelaEspecialidade;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Trrabalhor.Sistema; 
import telaPrincipal.TelaInicial; 
import Trabalhor.ConfirmacaoAgendamento;

public class Tela3 extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> comboEspecialidade, comboTipoAtendimento;
    private String hospitalSelecionado = "";
    private String enderecoSelecionado = "";
    private JButton ultimoBotaoSelecionado = null;

    public Tela3(Sistema usuarioLogado) {
        setTitle("SAVA - Selecionar Unidade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 750); 
        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        // --- TOPO AZUL ---
        JPanel topo = new JPanel();
        topo.setBackground(new Color(0, 87, 174));
        topo.setBounds(0, 0, 400, 60);
        topo.setLayout(null);
        contentPane.add(topo);

        JLabel lblTitulo = new JLabel("Especialidade e Unidade");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setBounds(50, 15, 300, 30);
        topo.add(lblTitulo);

        // --- SELEÇÕES ---
        comboEspecialidade = new JComboBox<>(new String[] {"Selecionar Especialidade", "Pediatria", "Ortopedia", "Oftalmologia", "Cardiologia"});
        comboEspecialidade.setBounds(25, 80, 340, 40);
        contentPane.add(comboEspecialidade);

        comboTipoAtendimento = new JComboBox<>(new String[] {"Selecionar Tipo", "Consulta Presencial", "Exame", "Retorno"});
        comboTipoAtendimento.setBounds(25, 130, 340, 40);
        contentPane.add(comboTipoAtendimento);

        JLabel lblUnidades = new JLabel("Unidades Disponíveis");
        lblUnidades.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblUnidades.setBounds(25, 190, 250, 20);
        contentPane.add(lblUnidades);

        // --- CARDS DE HOSPITAIS ---
        contentPane.add(criarCardHospital("Hospital Central", "Av. Paulista, 1000 - Bela Vista", 220));
        contentPane.add(criarCardHospital("Hospital São Paulo", "R. Napoleão de Barros, 715", 315));
        contentPane.add(criarCardHospital("Hospital Moriah", "Av. Miruna, 755 - Indianópolis", 410));
        contentPane.add(criarCardHospital("Blanc Hospital", "Av. dos Bandeirantes, 679", 505));

        // --- BOTÕES ---
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setBackground(new Color(0, 128, 255));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnConfirmar.setBounds(205, 630, 160, 45);
        btnConfirmar.addActionListener(e -> {
            String especialidade = (String) comboEspecialidade.getSelectedItem();
            String tipo = (String) comboTipoAtendimento.getSelectedItem();

            if (especialidade.equals("Selecionar Especialidade") || tipo.equals("Selecionar Tipo") || hospitalSelecionado.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos e escolha uma unidade!");
            } else {
                // REDIRECIONAMENTO PARA O CALENDÁRIO
                new ConfirmacaoAgendamento(hospitalSelecionado, enderecoSelecionado, especialidade, tipo, usuarioLogado).setVisible(true);
                dispose();
            }
        });
        contentPane.add(btnConfirmar);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds(25, 630, 160, 45);
        btnVoltar.addActionListener(e -> {
            new TelaInicial(usuarioLogado).setVisible(true);
            dispose();
        });
        contentPane.add(btnVoltar);
    }

    private JButton criarCardHospital(String nome, String endereco, int y) {
        JButton btn = new JButton();
        btn.setLayout(null);
        btn.setBounds(25, y, 340, 85);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblIcon = new JLabel(carregarIcone("/images/hospital.png", 40, 40));
        lblIcon.setBounds(15, 22, 40, 40);
        btn.add(lblIcon);

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNome.setBounds(70, 15, 200, 25);
        btn.add(lblNome);

        JLabel lblEnd = new JLabel(endereco);
        lblEnd.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblEnd.setForeground(Color.GRAY);
        lblEnd.setBounds(70, 40, 250, 20);
        btn.add(lblEnd);

        btn.addActionListener(e -> {
            if (ultimoBotaoSelecionado != null) {
                ultimoBotaoSelecionado.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
                ultimoBotaoSelecionado.setBackground(Color.WHITE);
            }
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 255), 2));
            btn.setBackground(new Color(240, 250, 255));
            hospitalSelecionado = nome;
            enderecoSelecionado = endereco;
            ultimoBotaoSelecionado = btn;
        });

        return btn;
    }

    private ImageIcon carregarIcone(String caminho, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(caminho);
            if (imgURL != null) {
                return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {}
        return null;
    }
}