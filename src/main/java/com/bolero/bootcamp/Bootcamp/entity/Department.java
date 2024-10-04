package com.bolero.bootcamp.Bootcamp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Department {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DEPARTMENT_SEQ_GEN")
    @SequenceGenerator(name = "DEPARTMENT_SEQ_GEN", sequenceName = "SEQ_DEPARTMENT_ID", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean mandatory;

    @Column(nullable = false)
    private Boolean readOnly;
}
