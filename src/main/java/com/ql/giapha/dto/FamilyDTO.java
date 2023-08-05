package com.ql.giapha.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDTO {
    private String name;
    private String address;
    private Long id;
    private Integer members;
}