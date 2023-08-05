package com.ql.giapha.dto;

import com.ql.giapha.model.RelationshipType;

import lombok.Data;

@Data
public class RelationshipDTO {
    private Long id;
    private RelationshipType type;
    private String name;

    // Constructor, getters, and setters...
}
