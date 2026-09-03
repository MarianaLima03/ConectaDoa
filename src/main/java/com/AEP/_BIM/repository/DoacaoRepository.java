package com.AEP._BIM.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.StatusDoacao;

public interface DoacaoRepository
        extends MongoRepository<Doacao, String> {

    List<Doacao> findByStatus(StatusDoacao status);
}