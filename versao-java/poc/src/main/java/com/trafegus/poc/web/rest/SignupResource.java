package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.services.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupResource {

    @Autowired
    private AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<?> signupUser(@RequestBody SignupDTO signupDTO) {
        UserDTO createdUser = authService.createUser(signupDTO);
        if (createdUser == null){
            return new ResponseEntity<>("User not created, come again later!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

}
