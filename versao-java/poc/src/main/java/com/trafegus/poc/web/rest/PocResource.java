package com.trafegus.poc.web.rest;

import com.trafegus.poc.model.Log;
import com.trafegus.poc.services.poc.PocServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class PocResource {

    @Autowired
    private PocServiceImpl pocService;

    @PostMapping("/poc")
    public ResponseEntity<Boolean> receberLogsEndpoint(@RequestBody Log logReceived) {
        log.info("Rest request for receiving logs");

        this.pocService.processLog(logReceived);

        return ResponseEntity.ok().body(true);
    }

}
