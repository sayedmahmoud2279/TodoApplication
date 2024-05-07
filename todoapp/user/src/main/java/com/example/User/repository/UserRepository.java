package com.example.User.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.User.model.User;



public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String pass);
    List<User> findByUsernameIgnoreCaseContaining(String username);

}
