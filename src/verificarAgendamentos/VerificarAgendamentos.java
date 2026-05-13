package verificarAgendamentos;

import java.util.ArrayList;

public class VerificarAgendamentos {
    private String especialidade;
    private String tipo;
    private String hospital;
    private String data;
    private String hora;
    private String status; 

    public static ArrayList<VerificarAgendamentos> listaAgendamentos = new ArrayList<>();

    public VerificarAgendamentos(String especialidade, String tipo, String hospital, String data, String hora) {
        this.especialidade = especialidade;
        this.tipo = tipo;
        this.hospital = hospital;
        this.data = data;
        this.hora = hora;
        this.status = "Agendado";
    }

    // Método para retornar o nome do arquivo de imagem baseado na especialidade
    public String getCaminhoImagem() {
        switch (especialidade.toLowerCase()) {
            case "pediatria": return "/images/baby.png";
            case "ortopedia": return "/images/orthopedics.png";
            case "oftalmologia": return "/images/searching.png";
            case "cardiologia": return "/images/cardiology.png";
            default: return "/images/hospital.png"; // fallback
        }
    }

    public String getEspecialidade() { return especialidade; }
    public String getTipo() { return tipo; }
    public String getHospital() { return hospital; }
    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}