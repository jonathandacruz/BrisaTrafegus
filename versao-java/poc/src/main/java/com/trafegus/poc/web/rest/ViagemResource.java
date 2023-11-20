package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.PermissaoEnum;
import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.model.ViagemDTO;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.viagem.ViagemServiceImpl;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
public class ViagemResource {

    @Autowired
    private ViagemServiceImpl viagemService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private ModelMapper modelMapper;

    private static final String USUARIO_LOGADO_LOG_MESSAGE = "USUARIO LOGADO: {}";
    private static final String USUARIO_SEM_PERMISSAO_READ = "Usuário não possui permissão para listar viagens.";
    private static final String USUARIO_SEM_PERMISSAO_WRITE = "Usuário não possui permissão para criar viagens.";
    private static final String USUARIO_SEM_PERMISSAO_SINISTRO = "Usuário não possui permissão para deletar viagens.";


    @GetMapping("/viagens/{id}")
    public ResponseEntity<Viagem> getOneViagemEndpoint(@PathVariable UUID id) {
        log.info("Rest request for listing client config with id: {}", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_READ)) {
            Viagem viagem = viagemService.findOne(id);

            if (viagem == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (Objects.equals(viagem.getEmpresaCNPJ(), usuario.getEmpresaCNPJ())) {
                    return ResponseEntity.ok().body(viagem);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } else {
            log.info(USUARIO_SEM_PERMISSAO_READ);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_READ);
        }
    }

    @GetMapping("/viagens")
    public ResponseEntity<List<Viagem>> getAllViagemEndpoint() {
        log.info("Rest request for listing all Viagens");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_READ)) {
            List<Viagem> viagens = viagemService.findAll(usuario.getEmpresaCNPJ());

            if (viagens.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().body(viagens);
            }
        } else {
            log.info(USUARIO_SEM_PERMISSAO_READ);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_READ);
        }
    }

    @PostMapping("/viagens")
    public ResponseEntity<Viagem> createOneViagemEndpoint(@RequestBody ViagemDTO viagemDTO) {
        log.info("Rest request for creating Viagem: {}", viagemDTO.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());

        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) || permissoesUsuario.contains(PermissaoEnum.VIAGEM_WRITE)) {
            Viagem viagem = this.modelMapper.map(viagemDTO, Viagem.class);
            viagem.setEmpresaCNPJ(usuario.getEmpresaCNPJ());
            Viagem createdViagem = viagemService.createOne(viagem);
            return ResponseEntity.ok().body(createdViagem);
        } else {
            log.info(USUARIO_SEM_PERMISSAO_WRITE);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_WRITE);
        }
    }

    @PostMapping("/viagens/{id}/sinistro")
    public ResponseEntity<Viagem> marcarViagemComoSinistroEndpoint(@PathVariable UUID id) {
        log.info("Rest request for marking Viagem with id: {} as sinistro", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(USUARIO_LOGADO_LOG_MESSAGE, authentication.getName());

        UserDTO usuario = this.authService.findUserByUsername(authentication.getName());
        Set<PermissaoEnum> permissoesUsuario = usuario.getPermissoes();

        if (permissoesUsuario.contains(PermissaoEnum.ADMIN) ||
                permissoesUsuario.contains(PermissaoEnum.VIAGEM_UPDATE)) {

            Viagem viagem = viagemService.findOne(id);
            if (Objects.equals(viagem.getEmpresaCNPJ(), usuario.getEmpresaCNPJ())) {
                Viagem viagemSinistro = viagemService.marcarViagemComoSinistro(id);
                if (viagemSinistro != null) {
                    return ResponseEntity.ok().body(viagemSinistro);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            log.info(USUARIO_SEM_PERMISSAO_SINISTRO);
            throw new MissingPermissionsException(USUARIO_SEM_PERMISSAO_SINISTRO);
        }
    }

}
