package com.trafegus.poc.services.viagem;

import com.trafegus.poc.model.Viagem;

import java.util.List;
import java.util.UUID;

public interface ViagemService {
    List<Viagem> findAll();
    Viagem findOne(UUID id);
    Viagem createOne(Viagem viagem);
    Viagem marcarViagemComoSinistro(UUID id);
}
