package com.trafegus.poc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 7, message = "A senha deve ter no mínimo 8 caracteres")
    private String password;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String numeroCPF;

    @Column(nullable = false)
    private String empresaCNPJ;

    private Set<PermissaoEnum> permissoes;

    @Column(nullable = false)
    private Boolean ativo;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

}
