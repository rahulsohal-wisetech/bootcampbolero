package com.bolero.bootcamp.Bootcamp.controller;

import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieves employees in paginated form.
     *
     * @return a list of employees in pagination.
     */
    @GetMapping
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            Pageable pageable = PageRequest.of(page, size);

            Page<Employee> employees = employeeService.getAllEmployees(pageable);
            return ResponseEntity.ok(employees);
    }

    /**
     * Retrieves an employee by ID.
     *
     * @param id the unique identifier of the employee
     * @return the employee with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
         Employee employee = employeeService.getEmployeeById(id);
         return ResponseEntity.ok(employee);

    }

    /**
     * Creates a new employee and assigns them to the default department.
     *
     * @param employee the employee object to be created
     * @return the created employee object
     */
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.saveEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Updates an existing employee.
     *
     * @param id       the unique identifier of the employee to be updated
     * @param employee the employee object containing updated information
     * @return the updated employee object
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * Deletes an employee by ID.
     *
     * @param id the unique identifier of the employee to be deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a department to an existing employee.
     *
     * @param employeeId the unique identifier of the employee to be updated
     * @param departmentId the unique identifier of the department to be updated
     * @return employee with added department.
     */
    @PutMapping("/{employeeId}/departments/{departmentId}")
    public ResponseEntity<?> addDepartmentToEmployee(@PathVariable Long employeeId, @PathVariable Long departmentId) {
        Employee updatedEmployee = employeeService.assignDepartmentToEmployee(employeeId, departmentId);
        return ResponseEntity.ok(updatedEmployee);
    }
}
