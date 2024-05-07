package com.example.Todo.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.todo.model.Task;

public interface TaskRepository extends JpaRepository<Task, Integer>{

}
