package com.AEP._BIM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.AEP._BIM.dto.DoacaoCreateRequest;
import com.AEP._BIM.dto.DoacaoResponse;
import com.AEP._BIM.dto.DoacaoSummaryResponse;
import com.AEP._BIM.dto.DoacaoUpdateRequest;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.mapper.DoacaoMapper;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.dto.ReservaRequest;


@Service
public class DoacaoService {

    private final DoacaoRepository repository;
    private final DoacaoMapper mapper;

    public DoacaoService(
            DoacaoRepository repository,
            DoacaoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    public List<DoacaoSummaryResponse> listar() {

        return repository
                .findAll()
                .stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public List<DoacaoSummaryResponse> listarDisponiveis() {

        return repository
                .findByStatus(StatusDoacao.DISPONIVEL)
                .stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public DoacaoResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModelPorId(id));
    }

    public DoacaoResponse criar(
            DoacaoCreateRequest request) {

        Doacao doacao = mapper.toModel(request);

        return mapper.toResponse(
                repository.save(doacao)
        );
    }

    public DoacaoResponse atualizar(
            String id,
            DoacaoUpdateRequest request) {

        Doacao doacao =
                buscarModelPorId(id);

        mapper.updateModel(request, doacao);

        return mapper.toResponse(
                repository.save(doacao)
        );
    }

    public void excluir(String id) {

        Doacao doacao =
                buscarModelPorId(id);

        repository.delete(doacao);
    }

    private Doacao buscarModelPorId(String id) {

        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new DoacaoNotFoundException(id)
                );
    }

    public DoacaoResponse reservar(
            String id,
            ReservaRequest request) {

        Doacao doacao = buscarModelPorId(id);

        if (doacao.getStatus() != StatusDoacao.DISPONIVEL) {
            throw new RuntimeException(
                    "Esta doação não está disponível."
            );
        }

        doacao.setReservadoPor(request.nome());
        doacao.setContatoReserva(request.contato());
        doacao.setStatus(StatusDoacao.RESERVADA);

        return mapper.toResponse(
                repository.save(doacao)
        );
    }
}