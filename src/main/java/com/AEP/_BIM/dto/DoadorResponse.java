package com.AEP._BIM.dto;

import java.util.List;

public record DoadorResponse(
        String id,
        String nome,
        String contato,
        List<EnderecoDto> enderecos) {
}
