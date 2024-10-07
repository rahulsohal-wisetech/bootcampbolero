package com.bolero.bootcamp.Bootcamp.repository;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d WHERE d.mandatory = true")
    Optional<Department> findMandatoryDepartment();
}

