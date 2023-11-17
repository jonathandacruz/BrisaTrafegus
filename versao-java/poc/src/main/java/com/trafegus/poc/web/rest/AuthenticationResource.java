package com.trafegus.poc.web.rest;

import com.trafegus.poc.dto.AuthenticationDTO;
import com.trafegus.poc.dto.AuthenticationResponse;
import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.dto.PasswordDTO;
import com.trafegus.poc.services.auth.AuthServiceImpl;
import com.trafegus.poc.services.jwt.UserDetailsServiceImpl;
import com.trafegus.poc.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class AuthenticationResource {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationDTO authenticationDTO, HttpServletResponse response) throws BadCredentialsException, DisabledException, UsernameNotFoundException, IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password!");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not activated");
            return null;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDTO.getUsername());

        UserDTO usuario = this.authService.findUserByUsername(authenticationDTO.getUsername());
        String jwt = "";

        if (authenticationDTO.getRememberMe()) {
            jwt = jwtUtil.generateToken(userDetails.getUsername(), usuario.getEmpresaCNPJ(), 10L);
        } else {
            jwt = jwtUtil.generateToken(userDetails.getUsername(), usuario.getEmpresaCNPJ(), 1L);
        }

        return ResponseEntity.ok().body(new AuthenticationResponse(jwt, usuario));

    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<UserDTO> changePassword(@RequestBody PasswordDTO passwordDto) {
        log.info("Rest request para alterar a senha, usuario = {}", passwordDto.getUsername());
        UserDTO usuario = this.authService.changePassword(passwordDto);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok().body(usuario);
        }
    }

}
