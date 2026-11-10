package com.AEP._BIM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.AEP._BIM.dto.InstituicaoRequest;
import com.AEP._BIM.dto.InstituicaoResponse;
import com.AEP._BIM.exception.InstituicaoNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.mapper.InstituicaoMapper;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.InstituicaoRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository repository;
    private final DoacaoRepository doacaoRepository;
    private final InstituicaoMapper mapper;

    public InstituicaoService(
            InstituicaoRepository repository,
            DoacaoRepository doacaoRepository,
            InstituicaoMapper mapper) {

        this.repository = repository;
        this.doacaoRepository = doacaoRepository;
        this.mapper = mapper;
    }

    public List<InstituicaoResponse> listar() {

        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public InstituicaoResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModelPorId(id));
    }

    public InstituicaoResponse criar(InstituicaoRequest request) {

        return mapper.toResponse(
                repository.save(mapper.toModel(request))
        );
    }

    public InstituicaoResponse atualizar(
            String id,
            InstituicaoRequest request) {

        Instituicao instituicao = buscarModelPorId(id);

        mapper.updateModel(request, instituicao);

        return mapper.toResponse(
                repository.save(instituicao)
        );
    }

    public void excluir(String id) {

        Instituicao instituicao = buscarModelPorId(id);

        if (doacaoRepository.existsByReservaInstituicaoId(id)) {
            throw new RegraNegocioException(
                    "Esta instituição possui reservas registradas."
            );
        }

        repository.delete(instituicao);
    }

    private Instituicao buscarModelPorId(String id) {

        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new InstituicaoNotFoundException(id)
                );
    }
}
