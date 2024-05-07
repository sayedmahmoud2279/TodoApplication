package com.example.Todo.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareResponse {
    private String fileType;
    private Integer fileId;
    private String view;
    private Boolean editable;
}
