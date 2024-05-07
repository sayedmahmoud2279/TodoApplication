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
public class TaskRequest {
    private Integer id;
    private String name;
    private String status;

    public Task mapToTask(){
        String statusRequest = this.getStatus() == null ? "pending" : this.getStatus();

        return Task.builder()
        .id(this.getId())
        .name(this.getName())
        .status(statusRequest)
        .build();
    }
}
