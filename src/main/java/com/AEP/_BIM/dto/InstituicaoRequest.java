package com.AEP._BIM.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstituicaoRequest(
        @NotBlank(message = "nome é obrigatório") String nome,
        @NotBlank(message = "contato é obrigatório") String contato,
        @NotNull(message = "endereço é obrigatório") @Valid EnderecoDto endereco) {
}
