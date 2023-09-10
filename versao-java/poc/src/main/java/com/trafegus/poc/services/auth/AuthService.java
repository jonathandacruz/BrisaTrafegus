package com.trafegus.poc.services.auth;

import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;

public interface AuthService {
    UserDTO createUser(SignupDTO signupDTO);
}
