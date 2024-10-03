package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.service.impl.DepartmentServiceImpl;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department validDepartment;
    private Department readOnlyDepartment;
    private Department updatedDepartment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        updatedDepartment = new Department();
        updatedDepartment.setId(3L);
        updatedDepartment.setName("DevelopmentProduct");
        updatedDepartment.setDefault(false);
        updatedDepartment.setReadOnly(false);
    }

    @Test
    void testGetDepartmentByIdSuccess() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(validDepartment));

        Department department = departmentService.getDepartmentById(1L);

        assertNotNull(department);
        assertEquals(validDepartment.getId(), department.getId());
        assertEquals(validDepartment.getName(), department.getName());
    }

    @Test
    void testGetDepartmentByIdNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.getDepartmentById(1L);
        });

        assertEquals("Department not found with ID: 1", exception.getMessage());
    }

    @Test
    void testCreateDepartmentSuccess() {
        when(departmentRepository.save(validDepartment)).thenReturn(validDepartment);

        Department createdDepartment = departmentService.createDepartment(validDepartment);

        assertNotNull(createdDepartment);
        assertEquals(validDepartment.getName(), createdDepartment.getName());
    }

    @Test
    void testGetAllDepartmentsWithPaginationSuccess() {
        List<Department> departments = Arrays.asList(validDepartment, readOnlyDepartment);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Department> paginatedDepartments = new PageImpl<>(departments, pageable, departments.size());

        when(departmentRepository.findAll(pageable)).thenReturn(paginatedDepartments);

        Page<Department> result = departmentService.getAllDepartments(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Development", result.getContent().get(0).getName());
        assertEquals("Organisation", result.getContent().get(1).getName());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testUpdateDepartmentSuccess() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(validDepartment));
        when(departmentRepository.save(updatedDepartment)).thenReturn(updatedDepartment);

        Department updatedDept = departmentService.updateDepartment(1L, updatedDepartment);

        assertNotNull(updatedDept);
        assertEquals(updatedDepartment.getName(), updatedDept.getName());
    }

    @Test
    void testUpdateDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.updateDepartment(1L, validDepartment);
        });

        assertEquals("Department not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteDepartmentSuccess() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.ofNullable(validDepartment));

        departmentService.deleteDepartment(1L);
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).delete(validDepartment);
    }

    @Test
    void testDeleteDepartmentNotFound() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.deleteDepartment(1L);
        });

        assertEquals("Department not found with ID: 1", exception.getMessage());
    }
}
