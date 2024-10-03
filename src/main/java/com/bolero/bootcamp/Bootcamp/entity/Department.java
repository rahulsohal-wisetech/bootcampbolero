package com.bolero.bootcamp.Bootcamp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Department {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(nullable = false)
    private boolean readOnly;
}
