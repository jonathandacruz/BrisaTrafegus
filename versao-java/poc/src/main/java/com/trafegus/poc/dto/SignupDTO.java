package com.trafegus.poc.dto;

import com.trafegus.poc.model.PermissaoEnum;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupDTO {

    private String username;
    private String email;
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String password;
    private String numeroCPF;
    private String empresaCNPJ;
    private String nomeCompleto;
    private Set<PermissaoEnum> permissoes;
    private Boolean ativo;

}
