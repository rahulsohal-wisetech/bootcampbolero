package com.bolero.bootcamp.Bootcamp.controller;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidDepartmentException;
import com.bolero.bootcamp.Bootcamp.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    private ObjectMapper objectMapper;
    private Department validDepartment;
    private Department invalidDepartment;
    private Department readOnlyDepartment;
    private Department defaultDepartment;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        validDepartment = new Department();
        validDepartment = new Department();
        validDepartment.setId(1L);
        validDepartment.setName("Development");
        validDepartment.setDefault(false);
        validDepartment.setReadOnly(false);

        readOnlyDepartment = new Department();
        readOnlyDepartment.setId(2L);
        readOnlyDepartment.setName("Organisation");
        readOnlyDepartment.setDefault(false);
        readOnlyDepartment.setReadOnly(true);

        invalidDepartment = new Department();
        invalidDepartment.setId(3L);
        invalidDepartment.setName(null);
        invalidDepartment.setDefault(false);
        invalidDepartment.setReadOnly(false);

        defaultDepartment = new Department();
        defaultDepartment.setId(4L);
        defaultDepartment.setName("Executive");
        defaultDepartment.setDefault(true);
        defaultDepartment.setReadOnly(false);
    }

    @Test
    public void testAddDepartmentWhenSuccess() throws Exception {
        when(departmentService.createDepartment(any(Department.class))).thenReturn(validDepartment);
        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDepartment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validDepartment.getId()))
                .andExpect(jsonPath("$.name").value(validDepartment.getName()));
    }

    @Test
    public void testAddDepartmentWhenInvalidDataThenFailure() throws Exception {
        when(departmentService.createDepartment(any(Department.class))).thenThrow(InvalidDepartmentException.class);
        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDepartment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetDepartmentByIdWhenSuccess() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(validDepartment);

        mockMvc.perform(get("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validDepartment.getId()))
                .andExpect(jsonPath("$.name").value(validDepartment.getName()));
    }

    @Test
    public void testGetDepartmentByIdWhenNotFoundThenFailure() throws Exception {
        when(departmentService.getDepartmentById(1L))
                .thenThrow(new InvalidDepartmentException("Department not found with ID: 1"));

        mockMvc.perform(get("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDepartmentReturnsSuccess() throws Exception {
        when(departmentService.updateDepartment(eq(1L), any(Department.class))).thenReturn(validDepartment);

        mockMvc.perform(put("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validDepartment.getId()))
                .andExpect(jsonPath("$.name").value("Development"));
    }

    @Test
    public void testUpdateDepartmentWhenReadOnlyThenFailure() throws Exception {
        when(departmentService.updateDepartment(eq(2L), any(Department.class)))
                .thenThrow(new IllegalArgumentException("Cannot update a read-only department"));

        mockMvc.perform(put("/api/v1/departments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readOnlyDepartment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateDepartmentWhenInvalidDataThenFailure() throws Exception {
        when(departmentService.updateDepartment(eq(3L), any(Department.class)))
                .thenThrow(new InvalidDepartmentException("Department not found with ID: 3"));

        mockMvc.perform(put("/api/v1/departments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepartment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteDepartmentWhenSuccess() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteDepartmentWhenReadOnlyThenFailure() throws Exception {
        doThrow(new InvalidDepartmentException("Cannot delete a read-only department"))
                .when(departmentService).deleteDepartment(2L);

        mockMvc.perform(delete("/api/v1/departments/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteDepartmentIfNotFoundThenFailure() throws Exception {
        doThrow(new DepartmentNotFoundException("Department not found with ID: 1"))
                .when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllDepartmentsWhenSuccess() throws Exception {
        // Mock paginated data
        List<Department> departments = Arrays.asList(validDepartment, readOnlyDepartment);
        Pageable pageable = PageRequest.of(0, 2);  // Page 0, 2 items per page
        Page<Department> departmentPage = new PageImpl<>(departments, pageable, departments.size());

        // Mock the paginated service call
        when(departmentService.getAllDepartments(any(Pageable.class))).thenReturn(departmentPage);

        // Perform the request with pagination parameters
        mockMvc.perform(get("/api/v1/departments")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(validDepartment.getId()))
                .andExpect(jsonPath("$.content[1].id").value(readOnlyDepartment.getId()))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
