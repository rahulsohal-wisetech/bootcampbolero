package com.bolero.bootcamp.Bootcamp.repository;

import com.bolero.bootcamp.Bootcamp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
