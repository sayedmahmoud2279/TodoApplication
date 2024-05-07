package com.example.Todo.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.todo.model.Todo;
import java.util.List;
import java.util.Optional;


public interface TodoRepository extends JpaRepository<Todo, Integer> {
    public List<Todo> findByFolderId(Integer folderId);
    public List<Todo> findByOwnerIdNot(Integer ownerId);
    public List<Todo> findByOwnerIdAndFolderIdNot(Integer ownerId, Integer folderId);
    public Optional<Todo> findByIdAndFolderId(Integer todoId, Integer folderId);
}
