package com.AEP._BIM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.AEP._BIM.dto.DoacaoRequest;
import com.AEP._BIM.dto.DoacaoResponse;
import com.AEP._BIM.dto.DoacaoSummaryResponse;
import com.AEP._BIM.dto.ReservaRequest;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.exception.DoadorNotFoundException;
import com.AEP._BIM.exception.InstituicaoNotFoundException;
import com.AEP._BIM.mapper.DoacaoMapper;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.DoadorRepository;
import com.AEP._BIM.repository.InstituicaoRepository;

@Service
public class DoacaoService {

    private final DoacaoRepository repository;
    private final DoadorRepository doadorRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final DoacaoMapper mapper;

    public DoacaoService(
            DoacaoRepository repository,
            DoadorRepository doadorRepository,
            InstituicaoRepository instituicaoRepository,
            DoacaoMapper mapper) {

        this.repository = repository;
        this.doadorRepository = doadorRepository;
        this.instituicaoRepository = instituicaoRepository;
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

    public List<DoacaoSummaryResponse> listarPorDoador(String doadorId) {

        buscarDoador(doadorId);

        return repository
                .findByDoadorId(doadorId)
                .stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public DoacaoResponse buscarPorId(String id) {
        return montarResposta(buscarModelPorId(id));
    }

    public DoacaoResponse criar(
            DoacaoRequest request) {

        buscarDoador(request.doadorId());

        Doacao doacao = mapper.toModel(request);

        return montarResposta(
                repository.save(doacao)
        );
    }

    public DoacaoResponse atualizar(
            String id,
            DoacaoRequest request) {

        Doacao doacao =
                buscarModelPorId(id);

        buscarDoador(request.doadorId());

        mapper.updateModel(request, doacao);

        return montarResposta(
                repository.save(doacao)
        );
    }

    public void excluir(String id) {

        Doacao doacao =
                buscarModelPorId(id);

        repository.delete(doacao);
    }

    public DoacaoResponse reservar(
            String id,
            ReservaRequest request) {

        Doacao doacao = buscarModelPorId(id);

        buscarInstituicao(request.instituicaoId());

        doacao.reservar(request.instituicaoId());

        return montarResposta(
                repository.save(doacao)
        );
    }

    public DoacaoResponse registrarRetirada(String id) {

        Doacao doacao = buscarModelPorId(id);

        doacao.registrarRetirada();

        return montarResposta(
                repository.save(doacao)
        );
    }

    private DoacaoResponse montarResposta(Doacao doacao) {

        Instituicao instituicao = null;

        if (doacao.getReserva() != null) {
            instituicao = buscarInstituicao(
                    doacao.getReserva().getInstituicaoId()
            );
        }

        return mapper.toResponse(
                doacao,
                buscarDoador(doacao.getDoadorId()),
                instituicao
        );
    }

    private Doacao buscarModelPorId(String id) {

        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new DoacaoNotFoundException(id)
                );
    }

    private Doador buscarDoador(String id) {

        return doadorRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new DoadorNotFoundException(id)
                );
    }

    private Instituicao buscarInstituicao(String id) {

        return instituicaoRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new InstituicaoNotFoundException(id)
                );
    }
}
