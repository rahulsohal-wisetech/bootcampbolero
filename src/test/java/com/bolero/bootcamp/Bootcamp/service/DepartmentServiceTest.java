package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
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
    public static final long NON_EXISTING_DEPARTMENT_ID = 500L;

    public static final long MANDATE_DEPARTMENT_ID = 3L;
    public static final String MANDATE_DEPARTMENT_NAME = "Management";
    public static final boolean MANDATE_DEPARTMENT_MANDATORY = true;
    public static final boolean MANDATE_DEPARTMENT_READONLY = false;

    public static final boolean MANDATE_DEPARTMENT_MANDATORY_UPDATED = false;

    public static final String DEPARTMENT_NOT_FOUND = "Department not found.";
    public static final String DEPARTMENT_CANNOT_BE_NULL_OR_EMPTY = "Department name cannot be null or empty";
    public static final String EXISTING_MANDATORY_DEPARTMENT = "Department creation failed due to existing mandatory department";

    @Mock
    private DepartmentRepository mockDepartmentRepository;

    private DepartmentService ref;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department validDepartment;
    private Department readOnlyDepartment;
    private Department updatedDepartment;
    private Department invalidDepartment;
    private Department mandateDepartment;
    private Department updateMandateDepartment;
    private Department updateMandateDepartment2;
    private Department updateReadOnlyDepartment;
    private Department updateInvalidDepartment;
    private Department updateNonExistingDepartment;
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

        mandateDepartment = new Department();
        mandateDepartment.setId(MANDATE_DEPARTMENT_ID);
        mandateDepartment.setName(MANDATE_DEPARTMENT_NAME);
        mandateDepartment.setMandatory(MANDATE_DEPARTMENT_MANDATORY);
        mandateDepartment.setReadOnly(MANDATE_DEPARTMENT_READONLY);

        updateMandateDepartment = new Department();
        updateMandateDepartment.setId(MANDATE_DEPARTMENT_ID);
        updateMandateDepartment.setName(MANDATE_DEPARTMENT_NAME);
        updateMandateDepartment.setMandatory(MANDATE_DEPARTMENT_MANDATORY_UPDATED);
        updateMandateDepartment.setReadOnly(MANDATE_DEPARTMENT_READONLY);

        updateMandateDepartment2 = new Department();
        updateMandateDepartment2.setId(VALID_DEPARTMENT_ID);
        updateMandateDepartment2.setName(VALID_DEPARTMENT_NAME);
        updateMandateDepartment2.setMandatory(MANDATE_DEPARTMENT_MANDATORY);
        updateMandateDepartment2.setReadOnly(VALID_DEPARTMENT_READONLY);

        updateReadOnlyDepartment = new Department();
        updateReadOnlyDepartment.setId(READONLY_DEPARTMENT_ID);
        updateReadOnlyDepartment.setName(READONLY_DEPARTMENT_NAME);
        updateReadOnlyDepartment.setMandatory(READONLY_DEPARTMENT_MANDATORY);
        updateReadOnlyDepartment.setReadOnly(UPDATED_DEPARTMENT_READONLY);

        updateInvalidDepartment = new Department();
        updateInvalidDepartment.setId(VALID_DEPARTMENT_ID);
        updateInvalidDepartment.setName(INVALID_DEPARTMENT_NAME);
        updateInvalidDepartment.setMandatory(INVALID_DEPARTMENT_MANDATORY);
        updateInvalidDepartment.setReadOnly(VALID_DEPARTMENT_READONLY);

        updateNonExistingDepartment = new Department();
        updateNonExistingDepartment.setId(NON_EXISTING_DEPARTMENT_ID);
        updateNonExistingDepartment.setName(VALID_DEPARTMENT_NAME);
        updateNonExistingDepartment.setMandatory(VALID_DEPARTMENT_MANDATORY);
        updateNonExistingDepartment.setReadOnly(VALID_DEPARTMENT_READONLY);

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
        lenient().when(mockDepartmentRepository.findById(MANDATE_DEPARTMENT_ID)).thenReturn(Optional.of(mandateDepartment));
        lenient().when(mockDepartmentRepository.findMandatoryDepartment()).thenReturn(Optional.of(mandateDepartment));
        lenient().when(mockDepartmentRepository.save(mandateDepartment)).thenReturn(mandateDepartment);
        lenient().when(mockDepartmentRepository.findById(READONLY_DEPARTMENT_ID)).thenReturn(Optional.of(readOnlyDepartment));
        lenient().when(mockDepartmentRepository.save(updateReadOnlyDepartment)).thenReturn(updateReadOnlyDepartment);
        lenient().when(mockDepartmentRepository.findById(NON_EXISTING_DEPARTMENT_ID)).thenReturn(Optional.empty());
        }

    @AfterEach
    void tearDown() {
        reset(mockDepartmentRepository);

    }

    @Test
    void getDepartmentById_ReturnsDepartment_WhenDepartmentExists() {
        Department department = ref.getDepartmentById(VALID_DEPARTMENT_ID);

        assertNotNull(department);
        assertEquals(VALID_DEPARTMENT_ID, department.getId());
        assertEquals(VALID_DEPARTMENT_NAME, department.getName());
    }

    @Test
    void getDepartmentById_ThrowsException_WhenDepartmentNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.getDepartmentById(INVALID_DEPARTMENT_ID);
        });
        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createDepartment_ReturnsCreatedDepartment_WhenInputIsValid() {
        Department createdDepartment = ref.createDepartment(validDepartment);

        assertNotNull(createdDepartment);
        assertEquals(VALID_DEPARTMENT_NAME, createdDepartment.getName());
    }

    @Test
    void createDepartment_ThrowsException_WhenInputIsInvalid() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.createDepartment(invalidDepartment);
        });
        assertEquals(DEPARTMENT_CANNOT_BE_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void getAllDepartmentsWithPagination_ReturnsDepartments_WhenCalledWithPagination() {
        Page<Department> result = ref.getAllDepartments(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(VALID_DEPARTMENT_NAME, result.getContent().get(0).getName());
        assertEquals(READONLY_DEPARTMENT_NAME, result.getContent().get(1).getName());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void updateDepartment_ReturnsUpdatedDepartment_WhenDepartmentExists() {
        Department updatedDept = departmentService.updateDepartment(UPDATED_DEPARTMENT_ID, updatedDepartment);

        assertNotNull(updatedDept);
        assertEquals(UPDATED_DEPARTMENT_NAME, updatedDept.getName());
    }

    @Test
    void updateDepartment_ThrowsException_WhenDepartmentNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.updateDepartment(INVALID_DEPARTMENT_ID, validDepartment);
        });

        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteDepartment_SuccessfullyDeletes_WhenDepartmentExists() {
        ref.deleteDepartment(VALID_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).findById(VALID_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).delete(validDepartment);
    }

    @Test
    void deleteDepartment_ThrowsException_WhenDepartmentNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.deleteDepartment(INVALID_DEPARTMENT_ID);
        });

        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateMandatoryDepartment_Success_WhenDepartmentExistsAndIsMandatory() {
        ref.updateDepartment(MANDATE_DEPARTMENT_ID, updateMandateDepartment);

        verify(mockDepartmentRepository, times(1)).findById(MANDATE_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).save(updateMandateDepartment);
    }

    @Test
    void updateDepartmentToMandatoryDepartment_Fails_WhenAnotherMandatoryExists() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.updateDepartment(VALID_DEPARTMENT_ID, updateMandateDepartment2);
        });
        assertEquals(EXISTING_MANDATORY_DEPARTMENT, exception.getMessage());
    }

    @Test
    void updateReadOnlyDepartment_Success_WhenDepartmentIsReadOnly() {
        ref.updateDepartment(READONLY_DEPARTMENT_ID, updateReadOnlyDepartment);
        verify(mockDepartmentRepository, times(1)).findById(READONLY_DEPARTMENT_ID);
        verify(mockDepartmentRepository, times(1)).save(updateReadOnlyDepartment);
    }

    @Test
    void updateReadOnlyDepartment_ThrowsException_WhenDepartmentNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.updateDepartment(NON_EXISTING_DEPARTMENT_ID, updateInvalidDepartment);
        });
        assertEquals(DEPARTMENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateDepartment_ThrowsException_WhenInputIsInvalid() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ref.updateDepartment(VALID_DEPARTMENT_ID, updateInvalidDepartment);
        });
        assertEquals(DEPARTMENT_CANNOT_BE_NULL_OR_EMPTY, exception.getMessage());
    }

}
