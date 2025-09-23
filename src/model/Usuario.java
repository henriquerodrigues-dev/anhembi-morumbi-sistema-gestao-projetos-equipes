package model;

import java.util.UUID; // Importa a classe UUID para gerar IDs únicos

public class Usuario {

    // Atributos
    private String id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String cargo;
    private String login;
    private String senha;

    // Construtor
    public Usuario(String nomeCompleto, String cpf, String email, String cargo, String login, String senha) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único para cada usuário
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
    }

}