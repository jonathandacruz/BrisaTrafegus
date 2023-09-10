package com.trafegus.poc.web.rest;

import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.services.viagem.ViagemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class ViagemResource {

    @Autowired
    private ViagemServiceImpl viagemService;

    @GetMapping("/viagens/{id}")
    public ResponseEntity<Viagem> getOneViagemEndpoint(@PathVariable UUID id) {
        log.info("Rest request for listing client config with id: {}", id.toString());

        Viagem viagem = viagemService.findOne(id);

        if (viagem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(viagem);
        }
    }

    @GetMapping("/viagens")
    public ResponseEntity<List<Viagem>> getAllViagemEndpoint() {
        log.info("Rest request for listing all Viagens");

        List<Viagem> viagens = viagemService.findAll();

        if (viagens.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(viagens);
        }
    }

    @PostMapping("/viagens")
    public ResponseEntity<Viagem> createOneViagemEndpoint(@RequestBody Viagem viagem) {
        log.info("Rest request for creating Viagem: {}", viagem.toString());
        Viagem createdViagem = viagemService.createOne(viagem);

        return ResponseEntity.ok().body(createdViagem);
    }

    @PostMapping("/viagens/{id}/sinistro")
    public ResponseEntity<Viagem> marcarViagemComoSinistroEndpoint(@PathVariable UUID id) {
        log.info("Rest request for marking Viagem with id: {} as sinistro", id.toString());
        Viagem viagem = viagemService.marcarViagemComoSinistro(id);

        return ResponseEntity.ok().body(viagem);
    }

}
