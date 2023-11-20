package com.trafegus.poc.util;

import com.trafegus.poc.web.exceptions.CPFInvalidoException;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import com.trafegus.poc.web.exceptions.PasswordException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    private static final String MESSAGE = "mensagem: ";

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handlePSQLException(PSQLException ex) {
        if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
            Map<String, String> errors = new HashMap<>();
            errors.put(MESSAGE, "Usuário já cadastrado!");
            return ResponseEntity.badRequest().body(errors);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(MissingPermissionsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String, String>> handleMissingPermissionsException(MissingPermissionsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handlePasswordException(PasswordException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, "Erro no campo: " + ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CPFInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleCPFInvalidoException(CPFInvalidoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

}
