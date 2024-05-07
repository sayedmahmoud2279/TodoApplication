package com.example.folder.folder.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderRequest {
    private Integer id;
    private String name;
    private Integer ownerId;
    private List<Integer> subFolders;
    private Integer parentFolder;

}
