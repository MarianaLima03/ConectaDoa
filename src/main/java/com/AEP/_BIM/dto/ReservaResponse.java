package com.AEP._BIM.dto;

import java.time.LocalDate;

public record ReservaResponse(
        String instituicaoId,
        String instituicaoNome,
        String instituicaoContato,
        LocalDate dataReserva,
        LocalDate dataRetirada
) {
}
