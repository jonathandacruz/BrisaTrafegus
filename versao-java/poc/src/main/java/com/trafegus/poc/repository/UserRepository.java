package com.trafegus.poc.repository;

import com.trafegus.poc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByUsername(String username);
    User findFirstByUsernameAndEmpresaCNPJ(String username, String empresaCNPJ);
    List<User> findAllByEmpresaCNPJ(String empresaCNPJ);
}
