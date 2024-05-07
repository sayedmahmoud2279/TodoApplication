package com.example.Todo.todo.model.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDTO {

    private String fileName;

    private Integer fileId;

    private String view;

    private Boolean editable;

    private List<Integer> usersId;
    
}
