package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.constant.Constants;
import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                    log.error(Constants.EMPLOYEE_NOT_FOUND, id);
                    return new EmployeeNotFoundException("Employee not found.");
                });
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees with pagination");
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        if(employee.getFirstName() == null || employee.getLastName() == null || employee.getFirstName().isEmpty() || employee.getLastName().isEmpty()) {
            log.error("FirstName or LastName is either empty or null!");
            throw new IllegalArgumentException("Invalid FirstName or LastName");
        }

        Department defaultDepartment = departmentRepository.findMandatoryDepartment()
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));

        employee.addDepartment(defaultDepartment);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        log.info("Updating employee with ID: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(Constants.EMPLOYEE_NOT_FOUND, id);
                    return new EmployeeNotFoundException("Employee not found.");
                });

        if(employeeDetails.getFirstName() == null || employeeDetails.getLastName() == null || employeeDetails.getFirstName().isEmpty() || employeeDetails.getLastName().isEmpty()) {
            log.error("FirstName or LastName is either empty or null!");
            throw new IllegalArgumentException("Invalid FirstName or LastName");
        }

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
            log.error(Constants.EMPLOYEE_NOT_FOUND, id);
            throw new EmployeeNotFoundException("Employee not found.");
        }

    }

    @Override
    public Employee assignDepartmentToEmployee(Long employeeId, Long departmentId) {
       Employee employee = employeeRepository.findById(employeeId)
               .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

       Department department = departmentRepository.findById(departmentId)
               .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + departmentId));

       employee.addDepartment(department);

       return employeeRepository.save(employee);
    }

    @Override
    public Employee unassignDepartmentFromEmployee(Long employeeId, Long departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + departmentId));

        if(employee.getDepartments().contains(department)) {
            employee.getDepartments().remove(department);
        } else {
            log.error("Employee - {} {} is not assigned to department: {}", employee.getFirstName(), employee.getLastName(), department.getName());
            throw new DepartmentNotFoundException("Department not found with ID: " + departmentId);
        }

        return employeeRepository.save(employee);
    }
}
