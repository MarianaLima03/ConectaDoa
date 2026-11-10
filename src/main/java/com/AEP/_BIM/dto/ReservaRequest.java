package com.AEP._BIM.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservaRequest(
        @NotBlank(message = "instituicaoId é obrigatório") String instituicaoId
) {
}
