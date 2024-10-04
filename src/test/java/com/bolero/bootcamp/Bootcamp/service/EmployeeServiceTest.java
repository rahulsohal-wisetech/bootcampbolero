package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidEmployeeException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.repository.EmployeeRepository;
import com.bolero.bootcamp.Bootcamp.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private Employee updateEmployee;
    private Employee invalidEmployeeWithFirstNameIsNull;
    private Employee invalidEmployeeWithLastNameIsNull;
    private Employee invalidEmployeeWithFirstNameIsEmpty;
    private Employee invalidEmployeeWithLastNameIsEmpty;

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
        readOnlyDepartment.setMandatory(true);
        readOnlyDepartment.setReadOnly(true);

        validDepartment = new Department();
        validDepartment.setId(2L);
        validDepartment.setName("Development");
        validDepartment.setMandatory(false);
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

        updateEmployee = new Employee();
        updateEmployee.setId(1L);
        updateEmployee.setFirstName("John");
        updateEmployee.setLastName("Wick");
        updateEmployee.setDepartments(validDepartmentsSetOne);

        invalidEmployeeWithFirstNameIsNull = new Employee();
        invalidEmployeeWithFirstNameIsNull.setId(3L);
        invalidEmployeeWithFirstNameIsNull.setFirstName(null);
        invalidEmployeeWithFirstNameIsNull.setLastName("Goodman");

        invalidEmployeeWithLastNameIsNull = new Employee();
        invalidEmployeeWithLastNameIsNull.setId(4L);
        invalidEmployeeWithLastNameIsNull.setFirstName("Alice");
        invalidEmployeeWithLastNameIsNull.setLastName(null);

        invalidEmployeeWithFirstNameIsEmpty = new Employee();
        invalidEmployeeWithFirstNameIsEmpty.setId(5L);
        invalidEmployeeWithFirstNameIsEmpty.setFirstName("");
        invalidEmployeeWithFirstNameIsEmpty.setLastName("Brown");

        invalidEmployeeWithLastNameIsEmpty = new Employee();
        invalidEmployeeWithLastNameIsEmpty.setId(6L);
        invalidEmployeeWithLastNameIsEmpty.setFirstName("Bob");
        invalidEmployeeWithLastNameIsEmpty.setLastName("");
    }

    @AfterEach
    void tearDown() {
        reset(employeeRepository, departmentRepository);

        validDepartmentsSetOne.clear();
        validDepartmentsSetTwo.clear();
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
        when(departmentRepository.findMandatoryDepartment()).thenReturn(Optional.ofNullable(readOnlyDepartment));
        when(employeeRepository.save(validEmployee)).thenReturn(validEmployee);

        Employee createdEmployee = employeeService.saveEmployee(validEmployee);

        assertNotNull(createdEmployee);
        assertEquals("John", createdEmployee.getFirstName());
    }

    @Test
    void testCreateEmployeeWhenFirstNameIsNull() {
        doThrow(InvalidEmployeeException.class).when(employeeRepository).save(invalidEmployeeWithFirstNameIsNull);
        RuntimeException exception = assertThrows(InvalidEmployeeException.class, () -> {
            employeeService.saveEmployee(invalidEmployeeWithFirstNameIsEmpty);
        });
        assertEquals("First Name should not be empty", exception.getMessage());
    }

    @Test
    void testUpdateEmployeeSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.save(validEmployee)).thenReturn(validEmployee);

        Employee updatedEmployee = employeeService.updateEmployee(1L, updateEmployee);

        assertNotNull(updatedEmployee);
        assertEquals("John", updatedEmployee.getFirstName());
        assertEquals("Wick", updatedEmployee.getLastName());
    }

    @Test
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(EmployeeNotFoundException.class, () -> {
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

        RuntimeException exception = assertThrows(EmployeeNotFoundException.class, () -> {
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

    @Test
    public void testAssignDepartmentToEmployeeSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(validDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);

        Employee result = employeeService.assignDepartmentToEmployee(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getDepartments().contains(validDepartment));
        verify(employeeRepository, times(1)).save(validEmployee);
    }

    @Test
    public void testAssignDepartmentToNonExistentEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.assignDepartmentToEmployee(1L, 2L);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testAssignNonExistentDepartmentToEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            employeeService.assignDepartmentToEmployee(1L, 2L);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignDepartmentFromEmployeeSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(validDepartment));

        validEmployee.getDepartments().add(validDepartment);
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);

        Employee result = employeeService.unassignDepartmentFromEmployee(1L, 2L);

        assertNotNull(result);
        assertFalse(result.getDepartments().contains(validDepartment));
        verify(employeeRepository, times(1)).save(validEmployee);
    }

    @Test
    public void testUnassignDepartmentNotAssignedToEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(validDepartment));

        validEmployee.getDepartments().clear();  // Ensure department is not assigned

        assertThrows(DepartmentNotFoundException.class, () -> {
            employeeService.unassignDepartmentFromEmployee(1L, 2L);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignDepartmentFromNonExistentEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.unassignDepartmentFromEmployee(1L, 2L);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignNonExistentDepartmentFromEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            employeeService.unassignDepartmentFromEmployee(1L, 2L);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testAssignAlreadyAssignedDepartmentToEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(validDepartment));

        validEmployee.getDepartments().add(validDepartment);
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);

        Employee result = employeeService.assignDepartmentToEmployee(1L, 2L);

        assertTrue(result.getDepartments().contains(validDepartment));  // Ensure no duplication
        verify(employeeRepository, times(1)).save(validEmployee);
    }

}
