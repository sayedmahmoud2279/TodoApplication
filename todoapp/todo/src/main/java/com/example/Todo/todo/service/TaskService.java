package com.example.Todo.todo.service;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.example.Todo.todo.model.Task;
import com.example.Todo.todo.model.Todo;
import com.example.Todo.todo.repository.TaskRepository;
import com.example.Todo.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final UtilityClass utilityClass;


    // Reusable functions [private]

    private MultiValueMap<String, String> buildQueryParams(Integer todoId, Integer userId){
        // Get Query Params
        String tempTodoId = String.valueOf(todoId);
        String tempUserId = String.valueOf(userId);
        // Build URL and get if user can edit
        Map<String, String> params = utilityClass.buildShareParams("todo", tempTodoId, tempUserId);
        return utilityClass.buildQueryParams(params);
    }

    private Todo getParent(Task task){
        return task.getTodo();
    }

    private Task getTask(Integer taskId){
        if (Objects.nonNull(taskId)){
            Optional<Task> task =  taskRepo.findById(taskId);
            if (task.isPresent()){
                return task.get();
            }
        }
        return null;
    }

    private Boolean canEditTask(Integer todoId, Integer userId){
        // Build URL and get if user can edit
        MultiValueMap<String, String> queryParams = this.buildQueryParams(todoId, userId);
        Boolean canEditTodo = utilityClass.canEditTodo(queryParams);

        return canEditTodo;
    }

    // Services
    
    public Task updateTask(Task task, Todo todo) {
        // get task 
        // System.out.println("Task : " + task);
        Task recordTask = this.getTask(task.getId());
        if (Objects.isNull(recordTask)){
            recordTask = this.createTask(task, todo);
            // System.out.println("Record : " + recordTask);
        }
        else{
            recordTask.setName(task.getName());
            recordTask.setStatus(task.getStatus());
            recordTask.setTodo(todo);
            taskRepo.save(recordTask);
        }
        return recordTask;

    }

    public Task updateStatus(Task task) {
        Task recordTask = this.getTask(task.getId());
        if (!Objects.isNull(recordTask)){
            recordTask.setStatus(task.getStatus());
            taskRepo.save(recordTask);
            return recordTask;

        }
        throw new IllegalAccessError("Task is not found!");

    }

    public Task createTask(Task task, Todo todo) {
        Task record = Task.builder()
        .name(task.getName())
        .status(task.getStatus())
        .todo(todo)
        .build();
        taskRepo.save(record);
        return record;
    }

    public Todo deleteTask(Integer taskId, Integer userId) {
        Optional<Task> optionalTask = taskRepo.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            Todo todo = getParent(task);
            Integer todoId =  todo.getId();
            Integer ownerId =  todo.getOwnerId();
            Boolean canEditTask = this.canEditTask(todoId, userId);
            Boolean isOwner = utilityClass.isOwner(userId, ownerId);

            // GET view of todo
            MultiValueMap<String, String> queryParams = this.buildQueryParams(todoId, userId);
            String todoView = utilityClass.getTodoView(queryParams);

            if (isOwner || (canEditTask && todoView != "organization")){
                taskRepo.deleteById(taskId);
            }
            return todo;

        }
        return null;

    }

}
