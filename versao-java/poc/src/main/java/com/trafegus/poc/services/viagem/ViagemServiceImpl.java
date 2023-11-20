package com.trafegus.poc.services.viagem;

import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.repository.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ViagemServiceImpl implements ViagemService {

    @Autowired
    private ViagemRepository viagemRepository;

    @Override
    public List<Viagem> findAll(String empresaCNPJ) {
        return viagemRepository.findAllByEmpresaCNPJ(empresaCNPJ);
    }

    @Override
    public Viagem findOne(UUID id) {
        return viagemRepository.findById(id).orElse(null);
    }

    @Override
    public Viagem createOne(Viagem viagem) {
        viagem.setId(UUID.randomUUID());
        viagem.setRegrasQuebradas(List.of());
        return viagemRepository.save(viagem);
    }

    @Override
    public Viagem marcarViagemComoSinistro(UUID id) {
        Viagem viagem = viagemRepository.findById(id).orElse(null);
        if (viagem != null) {
            viagem.setSinistro(true);
            return viagemRepository.save(viagem);
        } else {
            return null;
        }
    }

}
