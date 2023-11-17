package com.trafegus.poc.dto;

import lombok.Data;

@Data
public class AuthenticationDTO {

    private String username;
    private String password;
    private Boolean rememberMe;

}
