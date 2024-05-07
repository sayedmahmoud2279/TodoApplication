package com.example.Todo.todo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.example.Todo.todo.model.Todo;
import com.example.Todo.todo.model.dto.FolderRequest;
import com.example.Todo.todo.model.dto.ShareDTO;
import com.example.Todo.todo.model.dto.TodoRequest;
import com.example.Todo.todo.model.dto.TodoResponse;
import com.example.Todo.todo.service.TodoService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;




@RestController
@RequestMapping("folders/{folderId}/todo")
@RequiredArgsConstructor
@CrossOrigin

public class TodoController {

    private final TodoService todoService;

    // Create Todo
    @PostMapping()
    public TodoResponse createTodo(@RequestBody TodoRequest todoReq ,@RequestHeader Map<String,String> header) {
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");

        Todo todoList = todoReq.mapToTodo();
        TodoResponse response = TodoResponse.mapToTodoResponse(todoService.createTodo(todoList, userId, token));
        return response;
    }
    
    // Update Todo
    @PutMapping("/{todoId}")
    public TodoResponse updateTodo(@RequestBody TodoRequest todoReq, @PathVariable(name = "todoId") Integer id, @RequestHeader Map<String,String> header) {
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        Todo todoList = todoReq.mapToTodo();
        Todo updatedTodo = todoService.updateTodo(todoList, id, userId, token);
        System.out.println(updatedTodo.getTasks());
        TodoResponse response = TodoResponse.mapToTodoResponse(updatedTodo);
        return response;
    }
    // Get Todo
    @GetMapping("/{todoId}")
    public ResponseEntity<?> getTodoById(@PathVariable(name = "todoId") Integer id ,@RequestHeader Map<String,String> header) {
        try{
            Integer userId = Integer.parseInt(header.get("id"));
            String token = header.get("token");

            Todo todoList = todoService.getTodoById(id, userId, token);
            TodoResponse todoResponse = TodoResponse.mapToTodoResponse(todoList);
            return ResponseEntity.ok(todoResponse);

        }
         catch (Exception e) {
            return TodoResponse.handleInvalidRequest(400, e.toString());
        }
    }
    
    // Get All Todo
    @GetMapping
    public List<TodoResponse> getAllTodo(@PathVariable(name = "folderId") Integer folderId, @RequestParam String bound ,@RequestHeader Map<String,String> header) {
        // Get user id using JWT
        try {
            Integer userId = Integer.parseInt(header.get("id"));
            String token = header.get("token");
            List<TodoResponse> todoResponseList ;

            if(bound.equals("outside")){
                todoResponseList = todoService.getRestTodoLists(folderId, userId, token)
                .stream()
                .map(todoList -> TodoResponse.mapToTodoResponse(todoList))
                .toList();
            }
            else {

                todoResponseList = todoService.getTodoLists(folderId, userId, token)
                .stream()
                .map(todoList -> TodoResponse.mapToTodoResponse(todoList))
                .toList();
            }
            
            return todoResponseList;
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ArrayList<TodoResponse>();
        }
        
    }
    
    // Delete Todo
    @DeleteMapping("/{todoId}")
    public void deleteTodoList(@PathVariable(name = "todoId") Integer id, @PathVariable(name = "folderId") Integer folderId, @RequestHeader Map<String,String> header) {
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        todoService.delete(id, folderId, userId, token);
        // return deleteStatus;
    }

    @DeleteMapping
    public void deleteFolder(@PathVariable(name = "folderId") Integer folderId, @RequestHeader Map<String,String> header) {
        // Get user id using JWT
        // TODO : Trusted endpoint get it from folder service
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        todoService.deleteFolderById(folderId, userId, token);
        // return deleteStatus;
    }

    // Copy Todo List
    @PostMapping("/copy")
    public void copyTodoList(@RequestBody FolderRequest destination ,@RequestHeader Map<String,String> header) {
        //TODO: process POST request
        // Check if user owns destination folder
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        todoService.copyTodo(destination, userId, token);
    }

    // Move Todo List
    @PostMapping("/move")
    public void moveTodoList(@RequestBody FolderRequest folderReq ,@RequestHeader Map<String,String> header) {
        //TODO: process POST request
        // Check if user owns destination folder
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        todoService.moveTodo(folderReq, userId, token);
    }

    // Shourtcut Todo
    @PostMapping("/shortcut")
    public void addShortcut(@RequestBody FolderRequest destination, @PathVariable(name = "folderId") Integer source ,@RequestHeader Map<String,String> header) {
        //TODO: process POST request
        // Check if user owns destination folder
        Integer userId = Integer.parseInt(header.get("id"));
        String token = header.get("token");
        todoService.addShortcut(destination, userId, token);
    }


    // Share Todo
    @PostMapping("/share")
    public void shareTodo(@RequestBody ShareDTO shareSettings ,@RequestHeader Map<String,String> header) {
        //TODO: process POST request
        // Check if user owns destination folder
        Integer userId = Integer.parseInt(header.get("id"));

        todoService.shareTodo(shareSettings, userId);
    }

    // Get Shared
    @GetMapping("/shared")
    public List<TodoResponse> getSharedTodo(@RequestHeader Map<String,String> header) {
        Integer userId = Integer.parseInt(header.get("id"));
        List<Todo> todos = todoService.getSharedFolders(userId);
        return todos.stream().map(TodoResponse::mapToTodoResponse).toList();
    }
}
