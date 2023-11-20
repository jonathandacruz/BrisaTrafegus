package com.trafegus.poc.repository;

import com.trafegus.poc.model.Viagem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ViagemRepository extends MongoRepository<Viagem, UUID> {

    Optional<Viagem> findByCodigoViagemAndEmpresaCNPJ(String codigoViagem, String EmpresaCNPJ);
    List<Viagem> findAllByEmpresaCNPJ(String empresaCNPJ);
}
