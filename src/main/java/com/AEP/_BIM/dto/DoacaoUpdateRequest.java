package com.AEP._BIM.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DoacaoUpdateRequest(
        @NotBlank(message = "alimento é obrigatório") String alimento,
        @NotNull(message = "quantidade é obrigatória") @Positive(message = "quantidade deve ser maior que zero") Double quantidade,
        @NotBlank(message = "unidade é obrigatória") String unidade,
        @NotNull(message = "validade é obrigatória") @FutureOrPresent(message = "validade não pode estar no passado") LocalDate validade,
        @NotBlank(message = "doadorNome é obrigatório") String doadorNome,
        @NotBlank(message = "doadorContato é obrigatório") String doadorContato) {
}