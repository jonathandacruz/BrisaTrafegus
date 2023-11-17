package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.viagem.ViagemServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class ViagemResource {

    @Autowired
    private ViagemServiceImpl viagemService;

    @Autowired
    private AuthServiceImpl authService;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";

    @GetMapping("/viagens/{id}")
    public ResponseEntity<Viagem> getOneViagemEndpoint(@PathVariable UUID id) {
        log.info("Rest request for listing client config with id: {}", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_READ)) {
            Viagem viagem = viagemService.findOne(id);

            if (viagem == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().body(viagem);
            }
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

    @GetMapping("/viagens")
    public ResponseEntity<List<Viagem>> getAllViagemEndpoint() {
        log.info("Rest request for listing all Viagens");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_READ)) {
            List<Viagem> viagens = viagemService.findAll();

            if (viagens.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().body(viagens);
            }
        } else {
            log.info("Usuário não possui permissão para listar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar viagens.");
        }
    }

    @PostMapping("/viagens")
    public ResponseEntity<Viagem> createOneViagemEndpoint(@RequestBody Viagem viagem) {
        log.info("Rest request for creating Viagem: {}", viagem.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_WRITE)) {
            viagem.setEmpresaCNPJ(usuario.getEmpresaCNPJ());
            Viagem createdViagem = viagemService.createOne(viagem);
            return ResponseEntity.ok().body(createdViagem);
        } else {
            log.info("Usuário não possui permissão para criar viagens.");
            throw new MissingPermissionsException("Usuário não possui permissão para criar viagens.");
        }
    }

    @PostMapping("/viagens/{id}/sinistro")
    public ResponseEntity<Viagem> marcarViagemComoSinistroEndpoint(@PathVariable UUID id) {
        log.info("Rest request for marking Viagem with id: {} as sinistro", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario =
                this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_UPDATE)) {
            Viagem viagem = viagemService.marcarViagemComoSinistro(id);

            return ResponseEntity.ok().body(viagem);
        } else {
            log.info("Usuário não possui permissão para marcar viagens como sinistro.");
            throw new MissingPermissionsException("Usuário não possui permissão para marcar viagens como sinistro.");
        }
    }

}
