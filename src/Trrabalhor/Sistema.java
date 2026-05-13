package Trrabalhor;

import java.util.ArrayList;

public class Sistema {
    private String nome, email, cpf, senha;
    
    // NOVOS CAMPOS PARA ARMAZENAR O QUE FOI EDITADO
    private String nascimento = "";
    private String cidade = "";
    private String rua = "";
    private String telefone = "";

    public static ArrayList<Sistema> usuariosCadastrados = new ArrayList<>();

    public Sistema(String nome, String email, String cpf, String senha) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.senha = senha;
    }

    // Getters e Setters para os novos campos
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getSenha() { return senha; }

    public String getNascimento() { return nascimento; }
    public void setNascimento(String nascimento) { this.nascimento = nascimento; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}