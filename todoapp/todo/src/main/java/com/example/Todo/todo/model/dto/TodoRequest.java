package com.example.Todo.todo.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.Todo.todo.model.Task;
import com.example.Todo.todo.model.Todo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoRequest {

    private Integer id;
    private String name;
    private Integer ownerId;
    private Integer folderId;
    private List<TaskRequest> tasks;

    public Todo mapToTodo() {
        List<Task> tasksList = new ArrayList<Task>();
        try {
            tasksList = this.getTasks()
            .stream()
            .map(task -> task.mapToTask())
            .toList();
        } catch (Exception e) {
            // TODO: handle exception
        }
        


        return Todo.builder()
        .id(this.getId())
        .name(this.getName())
        .ownerId(this.getOwnerId())
        .folderId(this.getFolderId())
        .tasks(tasksList)
        .build();
    }

}
