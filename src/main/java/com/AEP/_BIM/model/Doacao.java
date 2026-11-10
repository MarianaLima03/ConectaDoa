package com.AEP._BIM.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.AEP._BIM.exception.RegraNegocioException;

@Document(collection = "doacoes")
public class Doacao {

    @Id
    private String id;
    private String alimento;
    private Double quantidade;
    private String unidade;
    private LocalDate validade;
    private String doadorId;
    private StatusDoacao status;
    private Reserva reserva;

    public Doacao(String id, String alimento, Double quantidade, String unidade,
                  LocalDate validade, String doadorId, StatusDoacao status) {

        this.id = id;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.validade = validade;
        this.doadorId = doadorId;
        this.status = status;
    }

    public void reservar(String instituicaoId) {

        if (status != StatusDoacao.DISPONIVEL) {
            throw new RegraNegocioException("Esta doação não está disponível.");
        }

        if (validade.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("Esta doação está vencida.");
        }

        reserva = new Reserva(instituicaoId, LocalDate.now());
        status = StatusDoacao.RESERVADA;
    }

    public void registrarRetirada() {

        if (status != StatusDoacao.RESERVADA) {
            throw new RegraNegocioException("Somente doações reservadas podem ser retiradas.");
        }

        reserva.setDataRetirada(LocalDate.now());
        status = StatusDoacao.RETIRADA;
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

    public String getDoadorId() {
        return doadorId;
    }

    public void setDoadorId(String doadorId) {
        this.doadorId = doadorId;
    }

    public StatusDoacao getStatus() {
        return status;
    }

    public Reserva getReserva() {
        return reserva;
    }
}
