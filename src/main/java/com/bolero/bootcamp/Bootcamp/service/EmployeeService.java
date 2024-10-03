package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface EmployeeService {

    /**
     * Retrieves an employee by their unique identifier.
     *
     * @param id the unique identifier of the employee
     * @return the employee with the specified id
     * @throws RuntimeException if no employee is found with the given id
     */
    Employee getEmployeeById(Long id);

    /**
     * Retrieves all employees from the database.
     *
     * @return a Paginated view of employees
     */
    Page<Employee> getAllEmployees(Pageable pageable);

    /**
     * Creates a new employee and assigns the default department to them.
     *
     * @param employee the employee object to be created
     * @return the created employee object with its generated id
     * @throws RuntimeException if the default department is not found
     */
    Employee saveEmployee(Employee employee);

    /**
     * Updates the details of an existing employee.
     *
     * @param id the unique identifier of the employee to be updated
     * @param employee the employee object containing updated information
     * @return the updated employee object
     * @throws RuntimeException if no employee is found with the given id
     */
    Employee updateEmployee(Long id, Employee employee);

    /**
     * Deletes an employee by their unique identifier.
     *
     * @param id the unique identifier of the employee to be deleted
     * @throws RuntimeException if no employee is found with the given id
     */
    void deleteEmployee(Long id);

    /**
     * Add employee to
     *
     * @param employeeId
     * @param departmentId
     * @return
     */
    @Transactional
    Employee addDepartmentToEmployee(Long employeeId, Long departmentId);
}
