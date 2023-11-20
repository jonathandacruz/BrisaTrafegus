package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigDTO;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.clientconfig.ClientConfigRedisServiceImpl;
import com.trafegus.poc.services.clientconfig.ClientConfigServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";
    private static final String USUARIO_SEM_PERMISSAO_READ = "Usuário não possui permissão para listar configurações.";
    private static final String USUARIO_SEM_PERMISSAO_WRITE = "Usuário não possui permissão para criar configurações.";
    private static final String USUARIO_SEM_PERMISSAO_DELETE = "Usuário não possui permissão para deletar configurações.";

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
            log.info(USUARIO_SEM_PERMISSAO_READ);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_READ);
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

            List<ClientConfig> clientConfigs = clientConfigService.findAll(usuario.getEmpresaCNPJ());

            if (clientConfigs.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().body(clientConfigs);
            }
        } else {
            log.info(USUARIO_SEM_PERMISSAO_READ);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_READ);
        }
    }

    @PostMapping("/configs")
    public ResponseEntity<ClientConfig> createConfigEndpoint(@RequestBody ClientConfigDTO clientConfigDTO) {
        log.info("Rest request for creating client config: {}", clientConfigDTO.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.CONFIG_WRITE)) {
            ClientConfig clientConfig = this.modelMapper.map(clientConfigDTO, ClientConfig.class);

            clientConfig.setEmpresaCNPJ(usuario.getEmpresaCNPJ());

            ClientConfig createdClientConfig = clientConfigService.createOne(clientConfig);
            log.info("Created config: {} in mongo.", createdClientConfig.toString());

            ClientConfigRedis redisConfig = this.clientConfigRedisService.findOne(createdClientConfig.getEmpresaCNPJ());

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
            log.info(USUARIO_SEM_PERMISSAO_WRITE);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_WRITE);
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
                Boolean redisRemoved = clientConfigRedisService.deleteOneConfig(clientConfig.getEmpresaCNPJ(), clientConfig);
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
            log.info(USUARIO_SEM_PERMISSAO_DELETE);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_DELETE);
        }
    }

}
