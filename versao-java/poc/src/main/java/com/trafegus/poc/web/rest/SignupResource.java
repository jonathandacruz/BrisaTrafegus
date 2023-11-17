package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SignupResource {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/registrar")
    public ResponseEntity<?> signupUser(@RequestBody SignupDTO signupDTO) {
        log.info("Rest request para criar um usuário.");
        UserDTO createdUser = authService.createUser(signupDTO);
        log.info("Usuário criado: {}", createdUser);
        if (createdUser == null){
            return new ResponseEntity<>("User not created, come again later!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

}
