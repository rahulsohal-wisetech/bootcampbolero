package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.repository.EmployeeRepository;
import com.bolero.bootcamp.Bootcamp.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;  // Assuming EmployeeServiceImpl is the implementation

    private Employee validEmployee;
    private Employee validEmployee2;

    private Department validDepartment;
    private Department readOnlyDepartment;

    private Set<Department> validDepartmentsSetOne;
    private Set<Department> validDepartmentsSetTwo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        readOnlyDepartment = new Department();
        readOnlyDepartment.setId(1L);
        readOnlyDepartment.setName("Organisation");
        readOnlyDepartment.setDefault(true);
        readOnlyDepartment.setReadOnly(true);

        validDepartment = new Department();
        validDepartment.setId(2L);
        validDepartment.setName("Development");
        validDepartment.setDefault(false);
        validDepartment.setReadOnly(false);

        validDepartmentsSetOne = new HashSet<>();
        validDepartmentsSetOne.add(readOnlyDepartment);

        validDepartmentsSetTwo = new HashSet<>();
        validDepartmentsSetTwo.add(readOnlyDepartment);
        validDepartmentsSetTwo.add(validDepartment);

        validEmployee = new Employee();
        validEmployee.setId(1L);
        validEmployee.setFirstName("John");
        validEmployee.setLastName("Smith");
        validEmployee.setDepartments(validDepartmentsSetOne);

        validEmployee2 = new Employee();
        validEmployee2.setId(2L);
        validEmployee2.setFirstName("Jane");
        validEmployee2.setLastName("Doe");
        validEmployee2.setDepartments(validDepartmentsSetTwo);
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));

        Employee employee = employeeService.getEmployeeById(1L);

        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("John", employee.getFirstName());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
    }

    @Test
    void testCreateEmployeeSuccess() {
        when(departmentRepository.findDefaultDepartment()).thenReturn(Optional.ofNullable(readOnlyDepartment));
        when(employeeRepository.save(validEmployee)).thenReturn(validEmployee);

        Employee createdEmployee = employeeService.saveEmployee(validEmployee);

        assertNotNull(createdEmployee);
        assertEquals("John", createdEmployee.getFirstName());
    }

    @Test
    void testUpdateEmployeeSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.save(validEmployee)).thenReturn(validEmployee);

        Employee updatedEmployee = employeeService.updateEmployee(1L, validEmployee);

        assertNotNull(updatedEmployee);
        assertEquals("John", updatedEmployee.getFirstName());
    }

    @Test
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployee(1L, validEmployee);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteEmployeeSuccess() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);
        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.deleteEmployee(1L);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetAllEmployeeWithPaginationSuccess() {
        List<Employee> employees = Arrays.asList(validEmployee, validEmployee2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Employee> paginatedEmployees = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(paginatedEmployees);

        Page<Employee> result = employeeService.getAllEmployees(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("Jane", result.getContent().get(1).getFirstName());
        assertEquals(2, result.getTotalElements());
    }
}
