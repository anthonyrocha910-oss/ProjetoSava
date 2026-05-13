package Trabalhor;

public class SistemaAgendamento {
    public String confirmar(String tipo, String data, String hora) {
        if (tipo == null || tipo.equals("Selecione...")) {
            return "Selecione o tipo de atendimento!";
        }
        if (data.contains("_") || hora.contains("_")) {
            return "Preencha data e hora corretamente!";
        }
        return "Agendamento confirmado:\nTipo: " + tipo + "\nData: " + data + "\nHora: " + hora;
    }
}