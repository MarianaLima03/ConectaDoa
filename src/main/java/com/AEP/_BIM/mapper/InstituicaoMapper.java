package com.AEP._BIM.mapper;

import org.springframework.stereotype.Component;

import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.dto.InstituicaoRequest;
import com.AEP._BIM.dto.InstituicaoResponse;
import com.AEP._BIM.model.Instituicao;

@Component
public class InstituicaoMapper {

    public Instituicao toModel(InstituicaoRequest request) {

        return new Instituicao(
                null,
                request.nome(),
                request.contato(),
                request.endereco().toModel()
        );
    }

    public void updateModel(
            InstituicaoRequest request,
            Instituicao instituicao) {

        instituicao.setNome(request.nome());
        instituicao.setContato(request.contato());
        instituicao.setEndereco(request.endereco().toModel());
    }

    public InstituicaoResponse toResponse(Instituicao instituicao) {

        return new InstituicaoResponse(
                instituicao.getId(),
                instituicao.getNome(),
                instituicao.getContato(),
                EnderecoDto.from(instituicao.getEndereco())
        );
    }
}
