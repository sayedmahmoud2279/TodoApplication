package com.example.Share.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Share.share.model.Todo_User;
import java.util.Optional;


public interface TodoUserRepository extends JpaRepository<Todo_User, Integer>{
    Optional<Todo_User> findByTodoIdAndUserId(Integer todoId, Integer userId);
    void deleteByTodoId(Integer todoId);
}
