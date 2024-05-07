package com.example.folder.folder.model.dto;

import java.util.Objects;

import com.example.folder.folder.model.Folder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderResponse {
    private Integer id;
    private Integer ownerId;
    private String name;
    private Integer parentFolder;

    public static FolderResponse makeFolderResponse(Folder folder){
        Integer parentFolder = Objects.nonNull(folder.getParentFolder()) ? folder.getParentFolder().getId() : null;
        // System.out.println(parentFolder);

        return FolderResponse.builder()
        .id(folder.getId())
        .name(folder.getName())
        .parentFolder(parentFolder)
        .ownerId(folder.getOwnerId())
        .build();
    }

    public static FolderResponse makeFolderResponse() {
        // TODO Auto-generated method stub
        return FolderResponse.builder().build();
    }
}
