package telaDados;

import Trrabalhor.Sistema;

public class dadosPessoais {
    // Dados que vêm do cadastro (bloqueado após o login)
    public String nome, cpf, email;
    
    // Dados que o usuário vai preencher na tela
    public String nascimento, cidade, rua, telefone;

    public dadosPessoais(Sistema u) {
        this.nome = u.getNome();
        this.cpf = u.getCpf();
        this.email = u.getEmail();
        this.nascimento = "";
        this.cidade = "";
        this.rua = "";
        this.telefone = "";
    }
}