package com.trafegus.poc.services.auth;

import com.trafegus.poc.dto.PasswordDTO;
import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;

import java.util.List;

public interface AuthService {
    UserDTO createUser(SignupDTO signupDTO);
    UserDTO findUserByUsername(String username);
    UserDTO findUserByUsernameAndEmpresaCNPJ(String username, String empresaCNPJ);
    List<UserDTO> findAllUsers(String empresaCNPJ);
    UserDTO updateUser(Long id, UserDTO userDTO, String empresaCNPJ);
    UserDTO deleteUser(Long id, String empresaCNPJ);
    UserDTO changePassword(PasswordDTO passwordDTO);
}
