package com.AEP._BIM.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record DoadorRequest(
        @NotBlank(message = "nome é obrigatório") String nome,
        @NotBlank(message = "contato é obrigatório") String contato,
        @NotEmpty(message = "informe pelo menos um endereço") List<@Valid EnderecoDto> enderecos) {
}
