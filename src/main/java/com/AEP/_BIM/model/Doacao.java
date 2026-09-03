package com.AEP._BIM.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doacoes")
public class Doacao {

    @Id
    private String id;
    private String alimento;
    private Double quantidade;
    private String unidade;
    private LocalDate validade;
    private String doadorNome;
    private String doadorContato;
    private StatusDoacao status;
    private String reservadoPor;
    private String contatoReserva;

    public Doacao(String id, String alimento, Double quantidade, String unidade,
                  LocalDate validade, String doadorNome, String doadorContato,
                  StatusDoacao status) {

        this.id = id;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.validade = validade;
        this.doadorNome = doadorNome;
        this.doadorContato = doadorContato;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getDoadorNome() {
        return doadorNome;
    }

    public void setDoadorNome(String doadorNome) {
        this.doadorNome = doadorNome;
    }

    public String getDoadorContato() {
        return doadorContato;
    }

    public void setDoadorContato(String doadorContato) {
        this.doadorContato = doadorContato;
    }

    public StatusDoacao getStatus() {
        return status;
    }

    public void setStatus(StatusDoacao status) {
        this.status = status;
    }

    public String getReservadoPor() {
        return reservadoPor;
    }

    public void setReservadoPor(String reservadoPor) {
        this.reservadoPor = reservadoPor;
    }

    public String getContatoReserva() {
        return contatoReserva;
    }

    public void setContatoReserva(String contatoReserva) {
        this.contatoReserva = contatoReserva;
    }
}