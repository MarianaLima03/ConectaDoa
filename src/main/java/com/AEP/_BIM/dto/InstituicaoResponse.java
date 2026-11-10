package com.AEP._BIM.dto;

public record InstituicaoResponse(
        String id,
        String nome,
        String contato,
        EnderecoDto endereco) {
}
