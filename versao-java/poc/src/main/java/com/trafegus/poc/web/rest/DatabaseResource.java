package com.trafegus.poc.web.rest;

import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
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

    @GetMapping("/database/clear")
    public ResponseEntity<Boolean> clearDatabaseEndpoint() {
        log.info("Rest request for clearing database");
        clientConfigRepository.deleteAll();
        clientConfigRedisRepository.deleteAll();
        return ResponseEntity.ok().body(true);
    }

}
