package com.AEP._BIM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.AEP._BIM.dto.DoadorRequest;
import com.AEP._BIM.dto.DoadorResponse;
import com.AEP._BIM.exception.DoadorNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.mapper.DoadorMapper;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.DoadorRepository;

@Service
public class DoadorService {

    private final DoadorRepository repository;
    private final DoacaoRepository doacaoRepository;
    private final DoadorMapper mapper;

    public DoadorService(
            DoadorRepository repository,
            DoacaoRepository doacaoRepository,
            DoadorMapper mapper) {

        this.repository = repository;
        this.doacaoRepository = doacaoRepository;
        this.mapper = mapper;
    }

    public List<DoadorResponse> listar() {

        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public DoadorResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModelPorId(id));
    }

    public DoadorResponse criar(DoadorRequest request) {

        return mapper.toResponse(
                repository.save(mapper.toModel(request))
        );
    }

    public DoadorResponse atualizar(
            String id,
            DoadorRequest request) {

        Doador doador = buscarModelPorId(id);

        mapper.updateModel(request, doador);

        return mapper.toResponse(
                repository.save(doador)
        );
    }

    public void excluir(String id) {

        Doador doador = buscarModelPorId(id);

        if (doacaoRepository.existsByDoadorId(id)) {
            throw new RegraNegocioException(
                    "Este doador possui doações cadastradas."
            );
        }

        repository.delete(doador);
    }

    private Doador buscarModelPorId(String id) {

        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new DoadorNotFoundException(id)
                );
    }
}
