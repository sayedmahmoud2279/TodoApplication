package com.example.Todo.todo.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.Todo.todo.model.Task;
import com.example.Todo.todo.model.Todo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoResponse {
    
    private Integer id;
    private Integer parentFolder;
    private String name;
    private List<TaskResponse> tasks;

    public static TodoResponse mapToTodoResponse(Todo todoList) {
        List<TaskResponse> tasksList = new ArrayList<TaskResponse>();
        try {
            tasksList = todoList.getTasks()
            .stream()
            .map(task -> TaskResponse.mapToTaskResponse(task))
            .toList();
        } catch (Exception e) {
            // TODO: handle exception
        }
        


        return TodoResponse.builder()
        .id(todoList.getId())
        .name(todoList.getName())
        .parentFolder(todoList.getFolderId())
        .tasks(tasksList)
        .build();
    }

    public static ResponseEntity<?> handleInvalidRequest(int statusCode, String errorMessage){
        return ResponseEntity.status(statusCode).body(errorMessage);
    }
}
