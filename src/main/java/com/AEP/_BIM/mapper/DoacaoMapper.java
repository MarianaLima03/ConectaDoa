package com.AEP._BIM.mapper;

import org.springframework.stereotype.Component;

import com.AEP._BIM.dto.DoacaoCreateRequest;
import com.AEP._BIM.dto.DoacaoResponse;
import com.AEP._BIM.dto.DoacaoSummaryResponse;
import com.AEP._BIM.dto.DoacaoUpdateRequest;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.StatusDoacao;

@Component
public class DoacaoMapper {

    public Doacao toModel(DoacaoCreateRequest request) {

        return new Doacao(
                null,
                request.alimento(),
                request.quantidade(),
                request.unidade(),
                request.validade(),
                request.doadorNome(),
                request.doadorContato(),
                StatusDoacao.DISPONIVEL
        );
    }

    public void updateModel(
            DoacaoUpdateRequest request,
            Doacao doacao) {

        doacao.setAlimento(request.alimento());
        doacao.setQuantidade(request.quantidade());
        doacao.setUnidade(request.unidade());
        doacao.setValidade(request.validade());
        doacao.setDoadorNome(request.doadorNome());
        doacao.setDoadorContato(request.doadorContato());
    }

    public DoacaoResponse toResponse(Doacao doacao) {

        return new DoacaoResponse(
                doacao.getId(),
                doacao.getAlimento(),
                doacao.getQuantidade(),
                doacao.getUnidade(),
                doacao.getValidade(),
                doacao.getDoadorNome(),
                doacao.getDoadorContato(),
                doacao.getStatus(),
                doacao.getReservadoPor(),
                doacao.getContatoReserva()
        );
    }

    public DoacaoSummaryResponse toSummaryResponse(
            Doacao doacao) {

        return new DoacaoSummaryResponse(
                doacao.getId(),
                doacao.getAlimento(),
                doacao.getStatus()
        );
    }
}