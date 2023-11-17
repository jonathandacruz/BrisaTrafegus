package com.trafegus.poc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.trafegus.poc.model.PermissaoEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String nomeCompleto;
    private String numeroCPF;
    private String empresaCNPJ;
    private Boolean ativo;
    private Set<PermissaoEnum> permissoes;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}
