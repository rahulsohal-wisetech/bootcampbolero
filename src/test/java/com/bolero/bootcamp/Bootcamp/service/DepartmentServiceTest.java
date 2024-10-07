package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.service.impl.DepartmentServiceImpl;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    public static final long VALID_DEPARTMENT_ID = 1L;
    public static final String VALID_DEPARTMENT_NAME = "Development";
    public static final boolean VALID_DEPARTMENT_MANDATORY = false;
    public static final boolean VALID_DEPARTMENT_READONLY = false;
    public static final long READONLY_DEPARTMENT_ID = 2L;
    public static final String READONLY_DEPARTMENT_NAME = "Organisation";
    public static final boolean READONLY_DEPARTMENT_MANDATORY = false;
    public static final boolean READONLY_DEPARTMENT_READONLY = true;
    public static final long UPDATED_DEPARTMENT_ID = 1L;
    public static final String UPDATED_DEPARTMENT_NAME = "DevOps";
    public static final boolean UPDATED_DEPARTMENT_MANDATORY = false;
    public static final boolean UPDATED_DEPARTMENT_READONLY = false;
    public static final long INVALID_DEPARTMENT_ID = 1000L;
    public static final String INVALID_DEPARTMENT_NAME = "";
    public static final Boolean INVALID_DEPARTMENT_MANDATORY = null;

    public static final String DEPARTMENT_NOT_FOUND = "Department not found.";
    public static final String DEPARTMENT_CANNOT_BE_NULL_OR_EMPTY = "Department name cannot be null or empty";

    @Mock
    private DepartmentRepository mockDepartmentRepository;

    private DepartmentService ref;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department validDepartment;
    private Department readOnlyDepartment;
    private Department updatedDepartment;
    private Department invalidDepartment;
    private Pageable pageable;
    private Page<Department> paginatedDepartments;
    private List<Department> departments;

    @BeforeEach
    void setUp() {

        validDepartment = new Department();
        validDepartment.setId(VALID_DEPARTMENT_ID);
        validDepartment.setName(VALID_DEPARTMENT_NAME);
        validDepartment.setMandatory(VALID_DEPARTMENT_MANDATORY);
        validDepartment.setReadOnly(VALID_DEPARTMENT_READONLY);

        readOnlyDepartment = new Department();
        readOnlyDepartment.setId(READONLY_DEPARTMENT_ID);
        readOnlyDepartment.setName(READONLY_DEPARTMENT_NAME);
        readOnlyDepartment.setMandatory(READONLY_DEPARTMENT_MANDATORY);
        readOnlyDepartment.setReadOnly(READONLY_DEPARTMENT_READONLY);

        updatedDepartment = new Department();
        updatedDepartment.setId(UPDATED_DEPARTMENT_ID);
        updatedDepartment.setName(UPDATED_DEPARTMENT_NAME);
        updatedDepartment.setMandatory(UPDATED_DEPARTMENT_MANDATORY);
        updatedDepartment.setReadOnly(UPDATED_DEPARTMENT_READONLY);

        invalidDepartment = new Department();
        invalidDepartment.setId(INVALID_DEPARTMENT_ID);
        invalidDepartment.setName(INVALID_DEPARTMENT_NAME);

        departments = Arrays.asList(validDepartment, readOnlyDepartment);
        pageable = PageRequest.of(0, 2);
        paginatedDepartments = new PageImpl<>(departments, pageable, departments.size());


        ref = new DepartmentServiceImpl(mockDepartmentRepository);

        lenient().when(mockDepartmentRepository.save(validDepartment)).thenReturn(validDepartment);
        lenient().when(mockDepartmentRepository.findById(VALID_DEPARTMENT_ID)).thenReturn(Optional.of(validDepartment));
        lenient().when(mockDepartmentRepository.findById(INVALID_DEPARTMENT_ID)).thenReturn(Optional.empty());
        lenient().when(mockDepartmentRepository.save(updatedDepartment)).thenReturn(updatedDepartment);
        lenient().when(mockDepartmentRepository.findAll(pageable)).thenReturn(paginatedDepartments);
        lenient().when(mockDepartmentRepository.existsById(INVALID_DEPARTMENT_ID)).thenReturn(false);

    }

    @AfterEach
    void tearDown() {
        reset(mockDepartmentRepository);

    }

    @Test
    void testGetDepartmentByIdSuccess() {
        Department department = ref.getDepartmentById(VALID_DEPARTMENT_ID);

        assertNotNull(department);
        assertEquals(VALID_DEPARTMENT_ID, department.getId());
        assertEquals(VALID_DEPARTMENT_NAME, department.getName());
    }

    @Test
    void testGetDepartmentByIdNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.getDepartmentById(INVALID_DEPARTMENT_ID);
        });
        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testCreateDepartmentSuccess() {

        Department createdDepartment = ref.createDepartment(validDepartment);

        assertNotNull(createdDepartment);
        assertEquals(VALID_DEPARTMENT_NAME, createdDepartment.getName());
    }

    @Test
    void testCreateDepartmentWithInvalidInput() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.createDepartment(invalidDepartment);
        });
        assertEquals(DEPARTMENT_CANNOT_BE_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void testGetAllDepartmentsWithPaginationSuccess() {

        Page<Department> result = ref.getAllDepartments(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(VALID_DEPARTMENT_NAME, result.getContent().get(0).getName());
        assertEquals(READONLY_DEPARTMENT_NAME, result.getContent().get(1).getName());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testUpdateDepartmentSuccess() {
        Department updatedDept = departmentService.updateDepartment(UPDATED_DEPARTMENT_ID, updatedDepartment);

        assertNotNull(updatedDept);
        assertEquals(UPDATED_DEPARTMENT_NAME, updatedDept.getName());
    }

    @Test
    void testUpdateDepartmentNotFound() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.updateDepartment(INVALID_DEPARTMENT_ID, validDepartment);
        });

        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testDeleteDepartmentSuccess() {

        ref.deleteDepartment(VALID_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).findById(VALID_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).delete(validDepartment);
    }

    @Test
    void testDeleteDepartmentNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.deleteDepartment(INVALID_DEPARTMENT_ID);
        });

        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }
}
