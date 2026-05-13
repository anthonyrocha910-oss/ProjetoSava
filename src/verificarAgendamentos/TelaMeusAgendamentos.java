package verificarAgendamentos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import Trrabalhor.Sistema;
import telaPrincipal.TelaInicial;
import TelaEspecialidade.Tela3;

public class TelaMeusAgendamentos extends JFrame {
    private JPanel contentPane;
    private JPanel painelLista;
    private JButton btnAcaoPrincipal, btnCancelarModo, btnVoltar;
    private boolean modoCancelamento = false;
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

    public TelaMeusAgendamentos(Sistema usuarioLogado) {
        setTitle("SAVA - Meus Agendamentos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 420, 700);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        JPanel topo = new JPanel();
        topo.setBackground(new Color(0, 102, 204));
        topo.setBounds(0, 0, 420, 60);
        contentPane.add(topo);
        JLabel lblTit = new JLabel("Meus Agendamentos", SwingConstants.CENTER);
        lblTit.setForeground(Color.WHITE);
        lblTit.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTit.setBounds(0, 15, 420, 30);
        topo.add(lblTit);

        painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setBounds(15, 75, 375, 400);
        scrollPane.setBorder(null);
        contentPane.add(scrollPane);

        btnCancelarModo = new JButton("CANCELAR CONSULTA");
        btnCancelarModo.setBounds(50, 490, 310, 35);
        btnCancelarModo.setBackground(new Color(255, 80, 80));
        btnCancelarModo.setForeground(Color.WHITE);
        contentPane.add(btnCancelarModo);

        btnAcaoPrincipal = new JButton("AGENDAR NOVA CONSULTA");
        btnAcaoPrincipal.setBounds(50, 535, 310, 40);
        btnAcaoPrincipal.setBackground(new Color(0, 128, 255));
        btnAcaoPrincipal.setForeground(Color.WHITE);
        contentPane.add(btnAcaoPrincipal);

        btnVoltar = new JButton("VOLTAR AO MENU");
        btnVoltar.setBounds(50, 585, 310, 35);
        contentPane.add(btnVoltar);

        atualizarInterface(usuarioLogado);

        btnAcaoPrincipal.addActionListener(e -> { new Tela3(usuarioLogado).setVisible(true); dispose(); });
        btnVoltar.addActionListener(e -> { new TelaInicial(usuarioLogado).setVisible(true); dispose(); });
        
        btnCancelarModo.addActionListener(e -> {
            if (!modoCancelamento) {
                modoCancelamento = true;
                btnCancelarModo.setText("CONFIRMAR SELECIONADOS");
                atualizarInterface(usuarioLogado);
            } else {
                processarCancelamento(usuarioLogado);
            }
        });
    }

    private void atualizarInterface(Sistema usuarioLogado) {
        painelLista.removeAll();
        checkBoxes.clear();

        ArrayList<VerificarAgendamentos> lista = VerificarAgendamentos.listaAgendamentos;

        if (lista.isEmpty()) {
            JLabel vazio = new JLabel("<html><center><br><br>Você não possui agendamentos.</center></html>", SwingConstants.CENTER);
            painelLista.add(vazio);
            btnCancelarModo.setVisible(false);
        } else {
            btnCancelarModo.setVisible(true);
            for (int i = 0; i < lista.size(); i++) {
                painelLista.add(criarCard(lista.get(i), i));
                painelLista.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        painelLista.revalidate();
        painelLista.repaint();
    }

    private JPanel criarCard(VerificarAgendamentos ag, int index) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(350, 130));
        card.setMaximumSize(new Dimension(350, 130));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (modoCancelamento && ag.getStatus().equals("Agendado")) {
            JCheckBox chk = new JCheckBox();
            chk.setBackground(Color.WHITE);
            checkBoxes.add(chk);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4;
            card.add(chk, gbc);
        } else { checkBoxes.add(null); }

        JLabel lblIcone = new JLabel(carregarIcone(ag.getCaminhoImagem(), 50, 50));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 4;
        card.add(lblIcone, gbc);

        JLabel lblEsp = new JLabel(ag.getEspecialidade());
        lblEsp.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblEsp.setForeground(new Color(0, 102, 204));
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0;
        card.add(lblEsp, gbc);

        JLabel lblHosp = new JLabel(ag.getHospital());
        lblHosp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        gbc.gridy = 1;
        card.add(lblHosp, gbc);

        JLabel lblDataHora = new JLabel(ag.getData() + "        " + ag.getHora());
        lblDataHora.setFont(new Font("Tahoma", Font.BOLD, 12));
        gbc.gridy = 2;
        card.add(lblDataHora, gbc);

        JLabel lblStatus = new JLabel(ag.getStatus().toUpperCase());
        lblStatus.setForeground(ag.getStatus().equals("Agendado") ? new Color(34, 139, 34) : Color.RED);
        lblStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
        gbc.gridy = 3;
        card.add(lblStatus, gbc);

        return card;
    }

    private void processarCancelamento(Sistema u) {
        int r = JOptionPane.showConfirmDialog(this, "Cancelar selecionados?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i) != null && checkBoxes.get(i).isSelected()) {
                    VerificarAgendamentos.listaAgendamentos.get(i).setStatus("Cancelado");
                }
            }
        }
        modoCancelamento = false;
        btnCancelarModo.setText("CANCELAR CONSULTA");
        atualizarInterface(u);
    }

    private ImageIcon carregarIcone(String c, int w, int h) {
        try {
            return new ImageIcon(new ImageIcon(getClass().getResource(c)).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }
}