package com.trafegus.poc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordDTO {

    @JsonProperty("username")
    private String username;

    @JsonProperty("oldPassword")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String oldPassword;

    @JsonProperty("newPassword")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String newPassword;
}
