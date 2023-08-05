package com.ql.giapha.dto;

import java.util.List;

import com.ql.giapha.model.Gender;

import lombok.Data;

@Data
public class PersonDTO {
    private Long id;
    private String name;
    private Gender gender;
    private List<RelationshipDTO> parents;
    private List<RelationshipDTO> siblings;
    private List<RelationshipDTO> spouses;
    private List<RelationshipDTO> children;

}