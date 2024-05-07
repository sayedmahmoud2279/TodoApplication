package com.example.Todo.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.todo.model.Todo_Folder;

public interface TodoFolderRepository extends JpaRepository<Todo_Folder, Integer> {
    Optional<Todo_Folder> findByTodoIdAndFolderId(Integer todoId, Integer folderId);
    List<Todo_Folder> findByFolderId(Integer folderId) ;
    List<Todo_Folder> findByTodoId(Integer todoId) ;
}
