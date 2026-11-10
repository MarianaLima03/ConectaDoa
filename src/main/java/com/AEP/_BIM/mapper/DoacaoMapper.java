package com.AEP._BIM.mapper;

import org.springframework.stereotype.Component;

import com.AEP._BIM.dto.DoacaoRequest;
import com.AEP._BIM.dto.DoacaoResponse;
import com.AEP._BIM.dto.DoacaoSummaryResponse;
import com.AEP._BIM.dto.ReservaResponse;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.model.Reserva;
import com.AEP._BIM.model.StatusDoacao;

@Component
public class DoacaoMapper {

    public Doacao toModel(DoacaoRequest request) {

        return new Doacao(
                null,
                request.alimento(),
                request.quantidade(),
                request.unidade(),
                request.validade(),
                request.doadorId(),
                StatusDoacao.DISPONIVEL
        );
    }

    public void updateModel(
            DoacaoRequest request,
            Doacao doacao) {

        doacao.setAlimento(request.alimento());
        doacao.setQuantidade(request.quantidade());
        doacao.setUnidade(request.unidade());
        doacao.setValidade(request.validade());
        doacao.setDoadorId(request.doadorId());
    }

    public DoacaoResponse toResponse(
            Doacao doacao,
            Doador doador,
            Instituicao instituicao) {

        return new DoacaoResponse(
                doacao.getId(),
                doacao.getAlimento(),
                doacao.getQuantidade(),
                doacao.getUnidade(),
                doacao.getValidade(),
                doador.getId(),
                doador.getNome(),
                doador.getContato(),
                doacao.getStatus(),
                toReservaResponse(doacao.getReserva(), instituicao)
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

    private ReservaResponse toReservaResponse(
            Reserva reserva,
            Instituicao instituicao) {

        if (reserva == null) {
            return null;
        }

        return new ReservaResponse(
                instituicao.getId(),
                instituicao.getNome(),
                instituicao.getContato(),
                reserva.getDataReserva(),
                reserva.getDataRetirada()
        );
    }
}
