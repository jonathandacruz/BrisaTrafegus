package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.clientconfig.ClientConfigRedisServiceImpl;
import com.trafegus.poc.services.clientconfig.ClientConfigServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class ClientConfigResource {

    @Autowired
    private ClientConfigServiceImpl clientConfigService;

    @Autowired
    private ClientConfigRedisServiceImpl clientConfigRedisService;

    @Autowired
    private AuthServiceImpl authService;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";

    @GetMapping("/configs/{id}")
    public ResponseEntity<ClientConfig> getOneConfigEndpoint(@PathVariable UUID id) {
        log.info("Rest request for listing client config with id: {}", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.CONFIG_READ)) {

            ClientConfig clientConfig = clientConfigService.findOne(id);

            if (clientConfig == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (Objects.equals(clientConfig.getEmpresaCNPJ(), usuario.getEmpresaCNPJ())) {
                    return ResponseEntity.ok().body(clientConfig);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

    @GetMapping("/configs")
    public ResponseEntity<List<ClientConfig>> getAllConfigsEndpoint() {
        log.info("Rest request for listing client configs");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());
        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.CONFIG_READ)) {

            List<ClientConfig> clientConfigs = clientConfigService.findAll();

            if (clientConfigs.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                clientConfigs.forEach(clientConfig -> {
                    if (!Objects.equals(clientConfig.getEmpresaCNPJ(), usuario.getEmpresaCNPJ())) {
                        clientConfigs.remove(clientConfig);
                    }
                });
                if (clientConfigs.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok().body(clientConfigs);
            }
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

    @PostMapping("/configs")
    public ResponseEntity<ClientConfig> createConfigEndpoint(@RequestBody ClientConfig clientConfig) {
        log.info("Rest request for creating client config: {}", clientConfig.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.CONFIG_WRITE)) {

            clientConfig.setEmpresaCNPJ(usuario.getEmpresaCNPJ());

            ClientConfig createdClientConfig = clientConfigService.createOne(clientConfig);
            log.info("Created config: {} in mongo.", createdClientConfig.toString());

            ClientConfigRedis redisConfig = this.clientConfigRedisService.findOne(createdClientConfig.getEmpresaId());

            if (redisConfig == null) {
                ClientConfigRedis newRedisConfig = clientConfigRedisService.createOne(createdClientConfig);
                log.info("Created config: {} in redis.", newRedisConfig.toString());
            } else {
                log.info(createdClientConfig.toString());
                log.info(redisConfig.toString());
                ClientConfigRedis updatedRedisConfig =
                        clientConfigRedisService.updateOne(createdClientConfig, redisConfig);
                log.info("Updated config: {} in redis.", updatedRedisConfig.toString());
            }

            return ResponseEntity.ok().body(createdClientConfig);
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Boolean> deleteConfigEndpoint(@PathVariable UUID id) {
        log.info("Rest request for deleting client config with id: {}", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());
        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.CONFIG_DELETE)) {

            ClientConfig clientConfig = clientConfigService.findOne(id);
            if (Objects.equals(clientConfig.getEmpresaCNPJ(), usuario.getEmpresaCNPJ())) {
                Boolean redisRemoved = clientConfigRedisService.deleteOneConfig(clientConfig.getEmpresaId(), clientConfig);
                Boolean mongoDeleted = clientConfigService.deleteOne(id);

                if (Boolean.TRUE.equals(redisRemoved && mongoDeleted)) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

}
