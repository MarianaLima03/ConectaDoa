package com.AEP._BIM.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.AEP._BIM.model.Instituicao;

public interface InstituicaoRepository
        extends MongoRepository<Instituicao, String> {
}
