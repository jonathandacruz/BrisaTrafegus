package com.trafegus.poc.repository;

import com.trafegus.poc.model.ClientConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientConfigRepository extends MongoRepository<ClientConfig, UUID> {

    @Query("{'regras.codigos': ?0, 'empresaCNPJ': ?1}")
    List<ClientConfig> findClientConfigByRegrasContainingAndEmpresaCNPJ(Integer codigo, String empresaCNPJ);

    List<ClientConfig> findAllByEmpresaCNPJ(String empresaCNPJ);

}
