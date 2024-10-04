package com.bolero.bootcamp.Bootcamp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "EMPLOYEE_SEQ_GEN")
    @SequenceGenerator(name = "EMPLOYEE_SEQ_GEN", sequenceName = "SEQ_EMPLOYEE_ID", allocationSize = 1)
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "EMPLOYEE_DEPARTMENT_MAP",
            joinColumns = @JoinColumn(name = "EMPLOYEE_ID"),
            inverseJoinColumns = @JoinColumn(name = "DEPARTMENT_ID")
    )
    private Set<Department> departments = new HashSet<>();

    public void addDepartment(Department department) {
        departments.add(department);
    }
}
