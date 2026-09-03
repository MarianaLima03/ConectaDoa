package com.AEP._BIM.dto;

import java.time.LocalDate;

import com.AEP._BIM.model.StatusDoacao;

public record DoacaoResponse(
        String id,
        String alimento,
        Double quantidade,
        String unidade,
        LocalDate validade,
        String doadorNome,
        String doadorContato,
        StatusDoacao status,
        String reservadoPor,
        String contatoReserva
) {
}
