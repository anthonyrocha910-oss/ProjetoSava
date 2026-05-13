package Trabalhor;

import javax.swing.*;
import java.awt.*;
import Trrabalhor.Sistema;
import TelaEspecialidade.Tela3;
import verificarAgendamentos.VerificarAgendamentos;

public class ConfirmacaoAgendamento extends JFrame {
    private static final long serialVersionUID = 1L;
    private String dataSelecionada = "";
    private String horaSelecionada = "";
    private JPanel painelHorarios;
    private JPanel painelCalendario;

    public ConfirmacaoAgendamento(String hospital, String endereco, String especialidade, String tipoConsulta, Sistema usuarioLogado) {
        setTitle("SAVA - Finalizar Agendamento");
        setSize(450, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);

        // --- TOPO AZUL ---
        JLabel titulo = new JLabel("Finalizar Agendamento", SwingConstants.CENTER);
        titulo.setBounds(0, 0, 450, 60);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(0, 102, 204));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        getContentPane().add(titulo);

        // --- RESUMO COM ÍCONE ---
        JPanel painelInfo = new JPanel();
        painelInfo.setBackground(new Color(230, 240, 250));
        painelInfo.setBounds(20, 75, 395, 110);
        painelInfo.setLayout(null);
        getContentPane().add(painelInfo);

        JLabel lblIcone = new JLabel(carregarIcone("/images/hospital.png", 50, 50));
        lblIcone.setBounds(15, 25, 50, 50);
        painelInfo.add(lblIcone);

        JLabel lblInfo = new JLabel("<html><b>Hospital:</b> " + hospital + 
                "<br><b>Endereço:</b> " + endereco + 
                "<br><b>Especialidade:</b> " + especialidade + 
                "<br><b>Tipo:</b> " + tipoConsulta + "</html>");
        lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblInfo.setBounds(80, 10, 300, 90);
        painelInfo.add(lblInfo);

        // --- CALENDÁRIO ---
        JLabel lblMes = new JLabel("Abril 2025", SwingConstants.CENTER);
        lblMes.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblMes.setBounds(20, 195, 395, 25);
        getContentPane().add(lblMes);

        painelCalendario = new JPanel(new GridLayout(0, 7, 5, 5));
        painelCalendario.setBackground(Color.WHITE);
        painelCalendario.setBounds(20, 225, 395, 180);
        
        String[] dias = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};
        for (String d : dias) painelCalendario.add(new JLabel(d, SwingConstants.CENTER));

        painelCalendario.add(new JLabel("")); painelCalendario.add(new JLabel(""));

        for (int i = 1; i <= 30; i++) {
            JButton btnDia = new JButton(String.valueOf(i));
            if (i == 10 || i == 15 || i == 22) {
                btnDia.setEnabled(false);
                btnDia.setBackground(new Color(255, 200, 200));
            } else {
                btnDia.setBackground(new Color(245, 245, 245));
                int d = i;
                btnDia.addActionListener(e -> {
                    dataSelecionada = String.format("%02d/04/2025", d);
                    atualizarHorarios();
                });
            }
            painelCalendario.add(btnDia);
        }
        getContentPane().add(painelCalendario);

        painelHorarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelHorarios.setBackground(Color.WHITE);
        painelHorarios.setBounds(20, 445, 395, 100);
        getContentPane().add(painelHorarios);

        // --- BOTÃO CONFIRMAR ---
        JButton btnConfirmar = new JButton("CONFIRMAR AGENDAMENTO");
        btnConfirmar.setBackground(new Color(50, 205, 50));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnConfirmar.setBounds(20, 565, 395, 50);
        btnConfirmar.addActionListener(e -> {
            if (dataSelecionada.isEmpty() || horaSelecionada.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione dia e horário!");
            } else {
                // ADICIONA NA LISTA GLOBAL
                VerificarAgendamentos novo = new VerificarAgendamentos(especialidade, tipoConsulta, hospital, dataSelecionada, horaSelecionada);
                VerificarAgendamentos.listaAgendamentos.add(novo);

                String msg = "<html><body style='width: 200px;'>✓ <b>AGENDAMENTO CONFIRMADO!</b><br><br>" +
                        "Hospital: " + hospital + "<br>Especialidade: " + especialidade + "<br>" +
                        "Data: " + dataSelecionada + "<br>Hora: " + horaSelecionada + "</body></html>";
                JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                new telaPrincipal.TelaInicial(usuarioLogado).setVisible(true);
                dispose();
            }
        });
        getContentPane().add(btnConfirmar);

        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setBounds(20, 625, 395, 40);
        btnVoltar.addActionListener(e -> { new Tela3(usuarioLogado).setVisible(true); dispose(); });
        getContentPane().add(btnVoltar);
    }

    private void atualizarHorarios() {
        painelHorarios.removeAll();
        String[] horas = {"16:00", "17:00", "18:00"};
        for (String h : horas) {
            JToggleButton b = new JToggleButton(h);
            b.addActionListener(e -> horaSelecionada = h);
            painelHorarios.add(b);
        }
        painelHorarios.revalidate();
        painelHorarios.repaint();
    }

    private ImageIcon carregarIcone(String caminho, int w, int h) {
        try {
            return new ImageIcon(new ImageIcon(getClass().getResource(caminho)).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }
}