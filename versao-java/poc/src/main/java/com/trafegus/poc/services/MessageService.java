package com.trafegus.poc.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPICO = "topico_viagens";

    public void sendMessage(String message) {
        log.info("Enviando mensagem: {}", message);
        kafkaTemplate.send(TOPICO, message);
    }

}
