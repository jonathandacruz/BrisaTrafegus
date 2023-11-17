package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.services.auth.AuthServiceImpl;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class UsuarioResource {

    @Autowired
    private AuthServiceImpl authService;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";

    @GetMapping("/usuarios")
    public ResponseEntity<List<UserDTO>> listarUsuarios() {
        log.info("Rest request para listar usuários");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            List<UserDTO> users = authService.findAllUsers();
            return ResponseEntity.ok(users);
        } else {
            log.info("Usuário não possui permissão para listar usuários.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar usuários.");
        }
    }

    @GetMapping("/usuarios/{username}")
    public ResponseEntity<UserDTO> listarUsuarioPorUsername(@PathVariable String username) {
        log.info("Rest request para listar usuários");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            UserDTO user = authService.findUserByUsername(username);
            return ResponseEntity.ok(user);
        } else {
            log.info("Usuário não possui permissão para listar usuários.");
            throw new MissingPermissionsException("Usuário não possui permissão para listar usuários.");
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UserDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("Rest request para atualizar usuários");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO userLogado = this.authService.findUserByUsername(authentication.getName());
        Set<PermissaoEnum> permissoesUsuario = userLogado.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            UserDTO user = authService.updateUser(id, userDTO);
            if (user == null) {
                log.info("Usuário não encontrado.");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } else {
            log.info("Usuário não possui permissão para atualizar usuários.");
            throw new MissingPermissionsException("Usuário não possui permissão para atualizar usuários.");
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<UserDTO> deletarUsuarioPorUsername(@PathVariable Long id) {
        log.info("Rest request para deletar usuários");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = this.authService.findUserByUsername(authentication.getName()).getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN)) {
            UserDTO user = authService.deleteUser(id);
            if (user == null) {
                log.info("Usuário não encontrado.");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } else {
            log.info("Usuário não possui permissão para deletar usuários.");
            throw new MissingPermissionsException("Usuário não possui permissão para deletar usuários.");
        }
    }

}
