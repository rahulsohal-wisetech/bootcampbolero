package com.bolero.bootcamp.Bootcamp.service.impl;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.repository.EmployeeRepository;
import com.bolero.bootcamp.Bootcamp.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with ID: " + id);
                });
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees with pagination");
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Department defaultDepartment = departmentRepository.findDefaultDepartment()
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));

        employee.addDepartment(defaultDepartment);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        log.info("Updating employee with ID: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with ID: " + id);
                });

        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        log.info("Successfully updated employee details for: {} {}", employeeDetails.getFirstName(), employeeDetails.getLastName());
        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        boolean doesEmployeeExists = employeeRepository.existsById(id);

        if (doesEmployeeExists) {
            employeeRepository.deleteById(id);
            log.info("Successfully deleted employee with ID: {}", id);
        } else {
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }

    }

    @Transactional
    @Override
    public Employee addDepartmentToEmployee(Long employeeId, Long departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + departmentId));

        employee.addDepartment(department); // Add department to employee's list of departments

        return employeeRepository.save(employee); // Save updated employee entity
    }
}
