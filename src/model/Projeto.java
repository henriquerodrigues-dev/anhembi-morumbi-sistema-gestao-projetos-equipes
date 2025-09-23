package model;

import java.time.LocalDate;
import java.util.UUID;

public class Projeto {

    // Atributos
    private String id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private String status;
    private Usuario gerenteResponsavel;

    // Construtor
    public Projeto(String nome, String descricao, LocalDate dataInicio, LocalDate dataTerminoPrevista, Usuario gerenteResponsavel) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = "planejado";
        this.gerenteResponsavel = gerenteResponsavel;
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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataTerminoPrevista() {
        return dataTerminoPrevista;
    }

    public String getStatus() {
        return status;
    }

    public Usuario getGerenteResponsavel() {
        return gerenteResponsavel;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) {
        this.dataTerminoPrevista = dataTerminoPrevista;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGerenteResponsavel(Usuario gerenteResponsavel) {
        this.gerenteResponsavel = gerenteResponsavel;
    }
}