package com.AEP._BIM.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doadores")
public class Doador {

    @Id
    private String id;
    private String nome;
    private String contato;
    private List<Endereco> enderecos;

    public Doador(String id, String nome, String contato, List<Endereco> enderecos) {
        this.id = id;
        this.nome = nome;
        this.contato = contato;
        this.enderecos = enderecos;
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

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }
}
