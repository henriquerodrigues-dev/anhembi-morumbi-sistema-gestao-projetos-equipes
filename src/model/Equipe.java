package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Equipe {

    // Atributos
    private String id;
    private String nome;
    private String descricao;
    private List<Usuario> membros;

    // Construtor
    public Equipe(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Métodos para manipulação da lista de membros
    public void adicionarMembro(Usuario usuario) {
        this.membros.add(usuario);
    }

    public void removerMembro(Usuario usuario) {
        this.membros.remove(usuario);
    }
}