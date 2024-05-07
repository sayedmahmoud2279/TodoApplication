package com.example.Todo.todo.controller;

import com.example.Todo.todo.model.dto.TaskResponse;
import com.example.Todo.todo.model.dto.TodoResponse;
import org.springframework.web.bind.annotation.*;

import com.example.Todo.todo.model.Todo;
import com.example.Todo.todo.service.TaskService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("folders/{folderId}/todo/{todoId}")
@RequiredArgsConstructor
@CrossOrigin

public class TaskController {

    private final TaskService taskService;
    // http://localhost:8080/folders/1/todo/7/task/12
    @DeleteMapping("/task/{taskId}")
    public TodoResponse deleteTask(@PathVariable Integer taskId, @RequestHeader Map<String, String> token) {

        Integer userId = -1;
        try {
            userId = Integer.parseInt(token.get("id"));
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error with token : " + token);
        }
        Todo todo = taskService.deleteTask(taskId, userId);
        return TodoResponse.mapToTodoResponse(todo);
    }


}
