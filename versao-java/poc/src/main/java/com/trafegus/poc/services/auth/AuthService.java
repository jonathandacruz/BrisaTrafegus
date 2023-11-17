package com.trafegus.poc.services.auth;

import com.trafegus.poc.dto.PasswordDTO;
import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;

import java.util.List;

public interface AuthService {
    UserDTO createUser(SignupDTO signupDTO);
    UserDTO findUserByUsername(String username);
    List<UserDTO> findAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO deleteUser(Long id);
    UserDTO changePassword(PasswordDTO passwordDTO);
}
