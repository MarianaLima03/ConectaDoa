package com.AEP._BIM.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.AEP._BIM.model.Doador;

public interface DoadorRepository
        extends MongoRepository<Doador, String> {
}
