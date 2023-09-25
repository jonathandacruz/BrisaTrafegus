package com.trafegus.poc.web.rest;

import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
import com.trafegus.poc.repository.ViagemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class DatabaseResource {

    @Autowired
    private ClientConfigRepository clientConfigRepository;

    @Autowired
    private ClientConfigRedisRepository clientConfigRedisRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @GetMapping("/database/clear")
    public ResponseEntity<Boolean> clearDatabaseEndpoint() {
        log.info("Rest request for clearing database");
        clientConfigRepository.deleteAll();
        clientConfigRedisRepository.deleteAll();
        viagemRepository.deleteAll();
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/database/redis")
    public ResponseEntity<ClientConfigRedis> getAllRedisConfigs() {
        log.info("Rest request for listing redis configs");
        ClientConfigRedis clientConfigRedis = clientConfigRedisRepository.findAll().iterator().next();
        return ResponseEntity.ok().body(clientConfigRedis);
    }

}
