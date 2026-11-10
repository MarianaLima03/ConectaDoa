package com.AEP._BIM.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.AEP._BIM.dto.DoadorRequest;
import com.AEP._BIM.dto.DoadorResponse;
import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Endereco;

@Component
public class DoadorMapper {

    public Doador toModel(DoadorRequest request) {

        return new Doador(
                null,
                request.nome(),
                request.contato(),
                toEnderecos(request)
        );
    }

    public void updateModel(
            DoadorRequest request,
            Doador doador) {

        doador.setNome(request.nome());
        doador.setContato(request.contato());
        doador.setEnderecos(toEnderecos(request));
    }

    public DoadorResponse toResponse(Doador doador) {

        return new DoadorResponse(
                doador.getId(),
                doador.getNome(),
                doador.getContato(),
                doador.getEnderecos()
                        .stream()
                        .map(EnderecoDto::from)
                        .toList()
        );
    }

    private List<Endereco> toEnderecos(DoadorRequest request) {

        return request.enderecos()
                .stream()
                .map(EnderecoDto::toModel)
                .toList();
    }
}
