package com.example.Share.share.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entity_permissions")
public class ShareSettings {
    @Id
    @GeneratedValue
    private Integer id;

    // [Entity Name - Object Id - View - Edit]
    private String fileName;

    private Integer fileId;

    private String view;

    private Boolean editable;


}
