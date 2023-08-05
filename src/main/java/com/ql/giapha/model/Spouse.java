package com.ql.giapha.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "spouse")
@Data
public class Spouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "idPerson")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "spouse_id", referencedColumnName = "idPerson")
    private Person spouse;

    @Enumerated(EnumType.STRING)
    private RelationshipType type;
    // Constructors, getters, setters, và các phương thức khác
}