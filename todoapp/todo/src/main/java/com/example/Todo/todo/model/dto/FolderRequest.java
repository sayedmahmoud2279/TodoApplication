package com.example.Todo.todo.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderRequest {

    private Integer id;
    private String name;
    private Integer ownerId;
    private Integer parentFolder;
    private List<Integer> todoIdList;

    
}
