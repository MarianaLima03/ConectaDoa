package com.AEP._BIM.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "instituicoes")
public class Instituicao {

    @Id
    private String id;
    private String nome;
    private String contato;
    private Endereco endereco;

    public Instituicao(String id, String nome, String contato, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.contato = contato;
        this.endereco = endereco;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
