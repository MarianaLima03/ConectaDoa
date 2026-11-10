package com.AEP._BIM.dto;

import com.AEP._BIM.model.Endereco;
import jakarta.validation.constraints.NotBlank;

public record EnderecoDto(
        @NotBlank(message = "rua é obrigatória") String rua,
        @NotBlank(message = "cidade é obrigatória") String cidade,
        @NotBlank(message = "estado é obrigatório") String estado) {

    public Endereco toModel() {
        return new Endereco(rua, cidade, estado);
    }

    public static EnderecoDto from(Endereco endereco) {
        return new EnderecoDto(
                endereco.getRua(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }
}
