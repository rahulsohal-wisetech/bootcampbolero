package com.bolero.bootcamp.Bootcamp.controller;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.entity.Employee;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee validEmployee;
    private Employee invalidEmployee;
    private Employee validEmployee2;
    private Employee invalidEmployee2;

    private Employee updatedEmployee;

    private Department validDepartment;
    private Department readOnlyDepartment;
    private Department invalidDepartment;

    private Set<Department> validDepartmentsSetOne;
    private Set<Department> validDepartmentsSetTwo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

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
        validDepartmentsSetTwo = new HashSet<>();
        validDepartmentsSetOne.add(readOnlyDepartment);

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

        updatedEmployee= new Employee();
        updatedEmployee.setId(2L);
        updatedEmployee.setFirstName("Janet");
        updatedEmployee.setLastName("Doe");
        updatedEmployee.setDepartments(validDepartmentsSetOne);

        invalidEmployee = new Employee();
        invalidEmployee.setId(null);
        invalidEmployee.setFirstName("");
        invalidEmployee.setLastName("Mitch");
        invalidEmployee.setDepartments(validDepartmentsSetOne);

        invalidEmployee2 = new Employee();
        invalidEmployee.setId(5L);
        invalidEmployee2.setFirstName("Jill");
        invalidEmployee2.setLastName("");
        invalidEmployee2.setDepartments(validDepartmentsSetTwo);
    }

    @Test
    public void testCreateEmployeeWhenSuccessWithDefaultDepartment() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(validEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value(validEmployee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(validEmployee.getLastName()));

        // Verify that the default department was assigned
        verify(employeeService).saveEmployee(argThat(employee ->
                employee.getDepartments().stream().anyMatch(department -> department.isDefault())));
    }

    @Test
    public void testGetListOfEmployeesWhenSuccess() throws Exception {
        // Mock paginated data
        List<Employee> employees = Arrays.asList(validEmployee, validEmployee2);
        Pageable pageable = PageRequest.of(0, 2);  // Page 0, 2 items per page
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        // Mock the paginated service call
        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(employeePage);

        // Perform the request with pagination parameters
        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(validEmployee.getId()))
                .andExpect(jsonPath("$.content[1].id").value(validEmployee2.getId()))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void testGetEmployeeByIdWhenSuccess() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(validEmployee);

        mockMvc.perform(get("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value(validEmployee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(validEmployee.getLastName()));
    }

    @Test
    public void testGetEmployeeByIdWhenNotFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenThrow(new EmployeeNotFoundException("{\"id\":1,\"Error\":\"Employee Not found\"}"));

        mockMvc.perform(get("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"id\":1,\"Error\":\"Employee Not found\"}"));
    }

    @Test
    public void testCreateEmployeeWhenSuccess() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(validEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value(validEmployee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(validEmployee.getLastName()));
    }

    @Test
    public void testCreateEmployeeWhenError() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class))).thenThrow(new RuntimeException("Failed to create employee"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validEmployee)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdateEmployeeWhenSuccess() throws Exception {

        when(employeeService.updateEmployee(anyLong(), any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value(updatedEmployee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedEmployee.getLastName()));
    }

    @Test
    public void testUpdateEmployeeWhenNotFound() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedEmployee)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEmployeeWhenSuccess() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteEmployeeWhenNotFound() throws Exception {
        doThrow(new RuntimeException("Employee not found")).when(employeeService).deleteEmployee(anyLong());

        mockMvc.perform(delete("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEmployeeWithInvalidData() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateEmployeeWithInvalidData() throws Exception {
        when(employeeService.updateEmployee(anyLong(), any(Employee.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    public void testAddDepartmentToEmployeeSuccess() throws Exception {
//
//        validEmployee.addDepartment(validDepartment);
//
//        when(employeeService.addDepartmentToEmployee(1L, 1L)).thenReturn(validEmployee);
//
//        mockMvc.perform(put("/api/employees/1/departments/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(validEmployee.getId()))
//                .andExpect(jsonPath("$.departments[0].id").value(validDepartment.getId()));
//    }

}
