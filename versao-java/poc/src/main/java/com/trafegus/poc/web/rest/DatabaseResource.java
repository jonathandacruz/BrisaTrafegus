package com.trafegus.poc.web.rest;

import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
import com.trafegus.poc.repository.ViagemRepository;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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

    @Autowired
    private AuthServiceImpl authService;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";

    @GetMapping("/database/clear")
    public ResponseEntity<Boolean> clearDatabaseEndpoint() {
        log.info("Rest request for clearing database");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            clientConfigRepository.deleteAll();
            clientConfigRedisRepository.deleteAll();
            viagemRepository.deleteAll();
            return ResponseEntity.ok().body(true);
        } else {
            log.info("Usuário não possui permissão para resetar o banco de dados.");
            throw new MissingPermissionsException("Usuário não possui permissão para resetar o banco de dados.");
        }
    }

    @GetMapping("/database/redis")
    public ResponseEntity<ClientConfigRedis> getAllRedisConfigs() {
        log.info("Rest request for listing redis configs");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            ClientConfigRedis clientConfigRedis = clientConfigRedisRepository.findAll().iterator().next();
            return ResponseEntity.ok().body(clientConfigRedis);
        } else {
            log.info("Usuário não possui permissão para listar as configurações do redis.");
            throw new MissingPermissionsException(
                    "Usuário não possui permissão para listar as configurações do redis.");
        }
    }

}
