package com.AEP._BIM.model;

import java.time.LocalDate;

public class Reserva {

    private String instituicaoId;
    private LocalDate dataReserva;
    private LocalDate dataRetirada;

    public Reserva(String instituicaoId, LocalDate dataReserva) {
        this.instituicaoId = instituicaoId;
        this.dataReserva = dataReserva;
    }

    public String getInstituicaoId() {
        return instituicaoId;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }
}
