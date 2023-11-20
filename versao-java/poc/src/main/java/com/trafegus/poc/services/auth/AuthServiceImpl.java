package com.trafegus.poc.services.auth;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.trafegus.poc.dto.PasswordDTO;
import com.trafegus.poc.dto.SignupDTO;
import com.trafegus.poc.dto.UserDTO;
import com.trafegus.poc.model.User;
import com.trafegus.poc.repository.UserRepository;
import com.trafegus.poc.web.exceptions.CPFInvalidoException;
import com.trafegus.poc.web.exceptions.MissingPermissionsException;
import com.trafegus.poc.web.exceptions.PasswordException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CPFValidator cpfValidator;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO createUser(SignupDTO signupDTO) {
        log.info("Criando novo usuário: {}", signupDTO);
        log.info("Validando CPF informado: {}", signupDTO.getNumeroCPF());
        if (this.validateCPF(signupDTO.getNumeroCPF())) {
            log.info("CPF informado é válido, prosseguindo com a criação.");
            if (signupDTO.getPassword().length() < 8) {
                log.info("Senha informada é muito curta, deve ter no mínimo 8 caracteres.");
                throw new PasswordException("Senha muito curta, deve ter no mínimo 8 caracteres.");
            }
            User user = new User();
            user.setUsername(signupDTO.getUsername());
            user.setEmail(signupDTO.getEmail());
            user.setPassword(new BCryptPasswordEncoder().encode(signupDTO.getPassword()));
            user.setNumeroCPF(signupDTO.getNumeroCPF());
            user.setEmpresaCNPJ(signupDTO.getEmpresaCNPJ());
            user.setNomeCompleto(signupDTO.getNomeCompleto());
            user.setAtivo(signupDTO.getAtivo());
            user.setPermissoes(signupDTO.getPermissoes());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            User createdUser = userRepository.save(user);
            UserDTO userDTO = new UserDTO();
            userDTO.setNomeCompleto(createdUser.getNomeCompleto());
            userDTO.setUsername(createdUser.getUsername());
            userDTO.setEmail(createdUser.getEmail());
            userDTO.setNumeroCPF(createdUser.getNumeroCPF());
            userDTO.setEmpresaCNPJ(createdUser.getEmpresaCNPJ());
            userDTO.setPermissoes(createdUser.getPermissoes());
            userDTO.setAtivo(createdUser.getAtivo());
            return userDTO;
        } else {
            log.info("CPF informado é inválido, {}", signupDTO.getNumeroCPF());
            return null;
        }
    }

    @Override
    public UserDTO changePassword(PasswordDTO passwordDTO) {
        if(passwordDTO.getNewPassword().length() < 8){
            log.info("Senha informada é muito curta, deve ter no mínimo 8 caracteres.");
            throw new PasswordException("Senha muito curta, deve ter no mínimo 8 caracteres.");
        } else {
            log.info("Alterando senha do usuário: {}", passwordDTO.getUsername());
            User user = userRepository.findFirstByUsername(passwordDTO.getUsername());
            if (user != null) {
                if (new BCryptPasswordEncoder().matches(passwordDTO.getOldPassword(), user.getPassword())) {
                    user.setPassword(new BCryptPasswordEncoder().encode(passwordDTO.getNewPassword()));
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUsername(user.getUsername());
                    userDTO.setNomeCompleto(user.getNomeCompleto());
                    log.info("Senha alterada com sucesso!");
                    return userDTO;
                } else {
                    log.info("Senha atual incorreta!");
                    throw new PasswordException("Senha atual incorreta!");
                }
            } else {
                log.info("Usuário não encontrado!");
                return null;
            }
        }
    }

    @Override
    public List<UserDTO> findAllUsers(String empresaCNPJ) {
        log.info("Listando todos os usuários.");
        List<User> users = userRepository.findAllByEmpresaCNPJ(empresaCNPJ);
        List<UserDTO> userDTOS = new ArrayList<>();
        users.forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO = this.createUserDTO(user);
            userDTOS.add(userDTO);
        });
        return userDTOS;
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO, String empresaCNPJ) {
        User usuario = this.userRepository.findById(id).orElse(null);
        if (usuario == null) {
            return null;
        } else {
            log.info("Checando se o usuário que está atualizando é da mesma empresa do usuário que está sendo atualizado.");
            if (!usuario.getEmpresaCNPJ().equals(empresaCNPJ)) {
                log.info("Usuário não pertence a mesma empresa do usuário que está atualizando.");
                return null;
            }
            log.info("Atualizando usuário: {}", usuario);
            log.info("Novas informações: {}", userDTO);
            this.modelMapper.getConfiguration().setSkipNullEnabled(true);
            this.modelMapper.map(userDTO, usuario);
            usuario.setUpdatedAt(LocalDateTime.now());
            if (userDTO.getPermissoes() != null) {
                usuario.setPermissoes(userDTO.getPermissoes());
            }
            User updatedUser = userRepository.save(usuario);
            return this.createUserDTO(updatedUser);
        }
    }

    @Override
    public UserDTO deleteUser(Long id, String empresaCNPJ) {
        User user = userRepository.findById(id).orElse(null);
        log.info("Deletando usuário: {}", user);
        if (user != null) {
            if (Objects.equals(user.getEmpresaCNPJ(), empresaCNPJ)) {
                userRepository.delete(user);
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUsername(user.getUsername());
                return userDTO;
            } else {
                log.info("Usuário não pertence a mesma empresa do usuário que está deletando.");
                return null;
            }
        }
        return null;
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.findFirstByUsername(username);
        if (user != null) {
            userDTO = this.createUserDTO(user);
        }
        return userDTO;
    }

    @Override
    public UserDTO findUserByUsernameAndEmpresaCNPJ(String username, String empresaCNPJ) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.findFirstByUsernameAndEmpresaCNPJ(username, empresaCNPJ);
        if (user != null) {
            userDTO = this.createUserDTO(user);
        }
        return userDTO;
    }

    private boolean validateCPF(String cpf) {
        try {
            this.cpfValidator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            log.info("CPF fornecido é inválido: {}", cpf);
            throw new CPFInvalidoException("CPF inválido: " + cpf);
        }
    }

    private UserDTO createUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
