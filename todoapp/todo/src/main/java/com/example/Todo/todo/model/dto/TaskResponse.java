package com.example.Todo.todo.model.dto;

import com.example.Todo.todo.model.Task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Integer id;
    private String name;
    private String status;

    public static TaskResponse mapToTaskResponse(Task task){

        return TaskResponse.builder()
        .id(task.getId())
        .name(task.getName())
        .status(task.getStatus())
        .build();
    }
}
