package telaPrincipal;

import Trrabalhor.Sistema;

public class TelaInicio {
    private Sistema usuario;

    public TelaInicio(Sistema usuario) {
        this.usuario = usuario;
    }

    public Sistema getUsuario() {
        return usuario;
    }
    
    public String getNomeBoasVindas() {
        return "Olá, " + usuario.getNome();
    }
}