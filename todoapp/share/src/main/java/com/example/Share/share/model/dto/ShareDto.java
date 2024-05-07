package com.example.Share.share.model.dto;


import java.util.List;

import com.example.Share.share.model.ShareSettings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareDto {
    private String fileName;

    private Integer fileId;

    private String view;

    private Boolean editable;

    private List<Integer> usersId;

    public ShareSettings mapToShareSettings(){
        return ShareSettings.builder()
        .fileName(this.getFileName())
        .fileId(this.getFileId())
        .view(this.getView())
        .editable(this.getEditable())
        .build();
    }
}
