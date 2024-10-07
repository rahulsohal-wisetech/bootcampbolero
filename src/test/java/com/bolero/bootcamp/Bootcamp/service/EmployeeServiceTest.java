package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.bolero.bootcamp.Bootcamp.service.DepartmentServiceTest.INVALID_DEPARTMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    public static final long READONLY_DEPARTMENT_ID = 1L;
    public static final String READONLY_DEPARTMENT_NAME = "Organisation";
    public static final boolean READONLY_DEPARTMENT_MANDATORY = true;
    public static final boolean READONLY_DEPARTMENT_READ_ONLY = true;
    public static final long VALID_DEPARTMENT_ID = 2L;
    public static final String VALID_DEPARTMENT_NAME = "Development";
    public static final boolean VALID_DEPARTMENT_MANDATORY = false;
    public static final boolean VALID_DEPARTMENT_READ_ONLY = false;
    public static final long VALID_EMPLOYEE_ID = 1L;
    public static final String VALID_EMPLOYEE_FIRST_NAME = "John";
    public static final String VALID_EMPLOYEE_LAST_NAME = "Smith";
    public static final long VALID_EMPLOYEE2_ID = 2L;
    public static final String VALID_EMPLOYEE2_FIRST_NAME = "Jane";
    public static final String VALID_EMPLOYEE2_LAST_NAME = "Doe";
    public static final long UPDATED_EMPLOYEE_ID = 1L;
    public static final String UPDATED_EMPLOYEE_FIRST_NAME = "John";
    public static final String UPDATED_EMPLOYEE_LAST_NAME = "Wick";
    public static final long INVALID_EMPLOYEE_ID = 100L;
    public static final String INVALID_EMPLOYEE_FIRSTNAME = null;
    public static final String INVALID_EMPLOYEE_LASTNAME = null;
    public static final String INVALID_FIRST_LASTNAME = "Invalid FirstName or LastName";
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found.";

    @Mock
    private EmployeeRepository mockEmployeeRepository;

    @Mock
    private DepartmentRepository mockDepartmentRepository;

    private EmployeeService ref;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee validEmployee;
    private Employee validEmployee2;
    private Employee updateEmployee;
    private Employee invalidEmployee;
    private Employee invalidEmployee2;

    private Department validDepartment;
    private Department readOnlyDepartment;

    private Set<Department> validDepartmentsSetOne;
    private Set<Department> validDepartmentsSetTwo;


    List<Employee> employees;
    Pageable pageable;
    Page<Employee> paginatedEmployees;

    @BeforeEach
    void setUp() {

        readOnlyDepartment = new Department();
        readOnlyDepartment.setId(READONLY_DEPARTMENT_ID);
        readOnlyDepartment.setName(READONLY_DEPARTMENT_NAME);
        readOnlyDepartment.setMandatory(READONLY_DEPARTMENT_MANDATORY);
        readOnlyDepartment.setReadOnly(READONLY_DEPARTMENT_READ_ONLY);

        validDepartment = new Department();
        validDepartment.setId(VALID_DEPARTMENT_ID);
        validDepartment.setName(VALID_DEPARTMENT_NAME);
        validDepartment.setMandatory(VALID_DEPARTMENT_MANDATORY);
        validDepartment.setReadOnly(VALID_DEPARTMENT_READ_ONLY);

        validDepartmentsSetOne = new HashSet<>();
        validDepartmentsSetOne.add(readOnlyDepartment);

        validDepartmentsSetTwo = new HashSet<>();
        validDepartmentsSetTwo.add(readOnlyDepartment);
        validDepartmentsSetTwo.add(validDepartment);

        validEmployee = new Employee();
        validEmployee.setId(VALID_EMPLOYEE_ID);
        validEmployee.setFirstName(VALID_EMPLOYEE_FIRST_NAME);
        validEmployee.setLastName(VALID_EMPLOYEE_LAST_NAME);
        validEmployee.setDepartments(validDepartmentsSetOne);

        validEmployee2 = new Employee();
        validEmployee2.setId(VALID_EMPLOYEE2_ID);
        validEmployee2.setFirstName(VALID_EMPLOYEE2_FIRST_NAME);
        validEmployee2.setLastName(VALID_EMPLOYEE2_LAST_NAME);
        validEmployee2.setDepartments(validDepartmentsSetTwo);

        updateEmployee = new Employee();
        updateEmployee.setId(UPDATED_EMPLOYEE_ID);
        updateEmployee.setFirstName(UPDATED_EMPLOYEE_FIRST_NAME);
        updateEmployee.setLastName(UPDATED_EMPLOYEE_LAST_NAME);
        updateEmployee.setDepartments(validDepartmentsSetOne);

        invalidEmployee = new Employee();
        invalidEmployee.setId(INVALID_EMPLOYEE_ID);
        invalidEmployee.setFirstName(INVALID_EMPLOYEE_FIRSTNAME);
        invalidEmployee.setLastName(INVALID_EMPLOYEE_LASTNAME);
        invalidEmployee.setDepartments(validDepartmentsSetOne);

        invalidEmployee2 = new Employee();
        invalidEmployee2.setId(VALID_EMPLOYEE_ID);
        invalidEmployee2.setFirstName(INVALID_EMPLOYEE_FIRSTNAME);
        invalidEmployee2.setLastName(INVALID_EMPLOYEE_LASTNAME);
        invalidEmployee2.setDepartments(validDepartmentsSetOne);

        employees = Arrays.asList(validEmployee, validEmployee2);
        pageable = PageRequest.of(0, 2);
        paginatedEmployees = new PageImpl<>(employees, pageable, employees.size());

        ref = new EmployeeServiceImpl(mockEmployeeRepository, mockDepartmentRepository);

        lenient().when(mockDepartmentRepository.save(validDepartment)).thenReturn(validDepartment);
        lenient().when(mockEmployeeRepository.save(validEmployee)).thenReturn(validEmployee);
        lenient().when(mockEmployeeRepository.findById(VALID_EMPLOYEE_ID)).thenReturn(Optional.of(validEmployee));
        lenient().when(mockEmployeeRepository.findById(INVALID_EMPLOYEE_ID)).thenReturn(Optional.empty());
        lenient().when(mockDepartmentRepository.findById(VALID_DEPARTMENT_ID)).thenReturn(Optional.of(validDepartment));
        lenient().when(mockDepartmentRepository.findMandatoryDepartment()).thenReturn(Optional.ofNullable(readOnlyDepartment));
        lenient().when(mockEmployeeRepository.existsById(VALID_EMPLOYEE_ID)).thenReturn(true);
        lenient().when(mockEmployeeRepository.existsById(INVALID_EMPLOYEE_ID)).thenReturn(false);
        lenient().when(mockEmployeeRepository.findAll(pageable)).thenReturn(paginatedEmployees);
        lenient().when(mockDepartmentRepository.findById(VALID_EMPLOYEE_ID)).thenReturn(Optional.of(validDepartment));
        lenient().when(mockEmployeeRepository.save(validEmployee)).thenReturn(validEmployee);

        lenient().when(mockDepartmentRepository.findById(VALID_DEPARTMENT_ID)).thenReturn(Optional.of(validDepartment));
        lenient().when(mockDepartmentRepository.findById(INVALID_DEPARTMENT_ID)).thenReturn(Optional.empty());

    }

    @AfterEach
    void tearDown() {
        reset(mockEmployeeRepository, mockDepartmentRepository);

        validDepartmentsSetOne.clear();
        validDepartmentsSetTwo.clear();
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        Employee employee = ref.getEmployeeById(VALID_EMPLOYEE_ID);

        assertNotNull(employee);
        assertEquals(VALID_EMPLOYEE_ID, employee.getId());
        assertEquals(VALID_EMPLOYEE_FIRST_NAME, employee.getFirstName());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            ref.getEmployeeById(INVALID_EMPLOYEE_ID);
        });

        assertEquals(EMPLOYEE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testCreateEmployeeSuccess() {

        Employee createdEmployee = ref.saveEmployee(validEmployee);

        assertNotNull(createdEmployee);
        assertEquals(VALID_EMPLOYEE_FIRST_NAME, createdEmployee.getFirstName());
    }

    @Test
    void testCreateEmployeeWhenInvalidInput() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ref.saveEmployee(invalidEmployee);
        });
        assertEquals(INVALID_FIRST_LASTNAME, exception.getMessage());
    }

    @Test
    void testUpdateEmployeeSuccess() {

        Employee updatedEmployee = ref.updateEmployee(UPDATED_EMPLOYEE_ID, updateEmployee);

        assertNotNull(updatedEmployee);
        assertEquals(UPDATED_EMPLOYEE_FIRST_NAME, updatedEmployee.getFirstName());
        assertEquals(UPDATED_EMPLOYEE_LAST_NAME, updatedEmployee.getLastName());
    }

    @Test
    void testUpdateEmployeeNotFound() {

        RuntimeException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            ref.updateEmployee(INVALID_EMPLOYEE_ID, updateEmployee);
        });

        assertEquals(EMPLOYEE_NOT_FOUND, exception.getMessage());
    }
    @Test
    void testUpdateEmployeeInvalidInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ref.updateEmployee(VALID_EMPLOYEE_ID, invalidEmployee2);
        });
        assertEquals(INVALID_FIRST_LASTNAME, exception.getMessage());
    }

    @Test
    void testDeleteEmployeeSuccess() {

        ref.deleteEmployee(VALID_EMPLOYEE_ID);
        verify(mockEmployeeRepository, times(1)).existsById(VALID_EMPLOYEE_ID);
        verify(mockEmployeeRepository, times(1)).deleteById(VALID_EMPLOYEE_ID);
    }

    @Test
    void testDeleteEmployeeNotFound() {

        RuntimeException exception = assertThrows(EmployeeNotFoundException.class, () -> ref.deleteEmployee(INVALID_EMPLOYEE_ID));

        assertEquals(EMPLOYEE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetAllEmployeeWithPaginationSuccess() {
        Page<Employee> result = ref.getAllEmployees(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(VALID_EMPLOYEE_FIRST_NAME, result.getContent().get(0).getFirstName());
        assertEquals(VALID_EMPLOYEE2_FIRST_NAME, result.getContent().get(1).getFirstName());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testAssignDepartmentToEmployeeSuccess() {

        Employee result = ref.assignDepartmentToEmployee(VALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID);

        assertNotNull(result);
        assertTrue(result.getDepartments().contains(validDepartment));
        verify(mockEmployeeRepository, times(1)).save(validEmployee);
    }

    @Test
    public void testAssignDepartmentToNonExistentEmployee() {

        assertThrows(EmployeeNotFoundException.class, () -> ref.assignDepartmentToEmployee(INVALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID));
        verify(mockEmployeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testAssignNonExistentDepartmentToEmployee() {
        assertThrows(DepartmentNotFoundException.class, () -> ref.assignDepartmentToEmployee(VALID_EMPLOYEE_ID, INVALID_DEPARTMENT_ID));
        verify(mockEmployeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignDepartmentFromEmployeeSuccess() {

        validEmployee.getDepartments().add(validDepartment);
        Employee result = ref.unassignDepartmentFromEmployee(VALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID);

        assertNotNull(result);
        assertFalse(result.getDepartments().contains(validDepartment));
        verify(mockEmployeeRepository, times(1)).save(validEmployee);
    }

    @Test
    public void testUnassignDepartmentNotAssignedToEmployee() {
        validEmployee.getDepartments().clear();

        assertThrows(DepartmentNotFoundException.class, () -> {
            ref.unassignDepartmentFromEmployee(VALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID);
        });

        verify(mockEmployeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignDepartmentFromNonExistentEmployee() {
        assertThrows(EmployeeNotFoundException.class, () -> {
            ref.unassignDepartmentFromEmployee(INVALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID);
        });

        verify(mockEmployeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUnassignNonExistentDepartmentFromEmployee() {
        assertThrows(DepartmentNotFoundException.class, () -> {
            ref.unassignDepartmentFromEmployee(VALID_EMPLOYEE_ID, INVALID_DEPARTMENT_ID);
        });

        verify(mockEmployeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testAssignAlreadyAssignedDepartmentToEmployee() {
        validEmployee.getDepartments().add(validDepartment);
        Employee result = ref.assignDepartmentToEmployee(VALID_EMPLOYEE_ID, VALID_DEPARTMENT_ID);

        assertTrue(result.getDepartments().contains(validDepartment));
        verify(mockEmployeeRepository, times(1)).save(validEmployee);
    }

}
