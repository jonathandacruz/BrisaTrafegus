package com.trafegus.poc.web.rest;

import com.trafegus.poc.model.Log;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.poc.PocServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class PocResource {

    @Autowired
    private PocServiceImpl pocService;

    @Autowired
    private AuthServiceImpl authService;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";

    @PostMapping("/poc")
    public ResponseEntity<Boolean> receberLogsEndpoint(@RequestBody Log logReceived) {
        log.info("Rest request for receiving logs");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.SEND_LOGS)) {

            Boolean shouldProcess = this.pocService.processLog(logReceived);

            return ResponseEntity.ok().body(shouldProcess);
        } else {
            log.info("Usuário não possui permissão para enviar logs.");
            throw new MissingPermissionsException("Usuário não possui permissão para enviar logs.");
        }
    }

}
