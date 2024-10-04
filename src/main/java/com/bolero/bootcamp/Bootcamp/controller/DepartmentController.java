package com.bolero.bootcamp.Bootcamp.controller;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidDepartmentException;
import com.bolero.bootcamp.Bootcamp.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Creates a new department.
     *
     * @param department the department object to be created
     * @return the created department object
     */
    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        departmentService.createDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(department);
    }

    /**
     * Retrieves departments in paginated form.
     *
     * @return a list of departments in pagination.
     */
    @GetMapping
    public ResponseEntity<Page<Department>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(departmentService.getAllDepartments(pageable));
    }

    /**
     * Retrieves a department by ID.
     *
     * @param id the unique identifier of the department
     * @return the department with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
       return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * Updates an existing department.
     *
     * @param id       the unique identifier of the department to be updated
     * @param department the department object containing updated information
     * @return the updated department object
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    /**
     * Deletes a department by ID.
     *
     * @param id the unique identifier of the department to be deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
