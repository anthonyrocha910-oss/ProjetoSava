package editarDados;

import Trrabalhor.Sistema;

public class EditarDados {
    private Sistema usuario;

    public EditarDados(Sistema usuario) {
        this.usuario = usuario;
    }

    // Método para simular o salvamento 
    public boolean salvarNovosDados(String nascimento, String cidade, String rua, String telefone) {
        // atualizar o objeto usuario ou uma lista
        if (nascimento.isEmpty() || cidade.isEmpty()) {
            return false;
        }
        return true;
    }

    public Sistema getUsuario() {
        return usuario;
    }
}