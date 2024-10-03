package com.bolero.bootcamp.Bootcamp.service;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {

    /**
     * Retrieves a department by its unique identifier.
     *
     * @param id the unique identifier of the department
     * @return the department with the specified id
     * @throws RuntimeException if no department is found with the given id
     */
    public Department getDepartmentById(Long id);

    /**
     * Retrieves Paginated view of departments from the database.
     *
     * @return a list of requested pages departments
     */
    public Page<Department> getAllDepartments(Pageable pageable);

    /**
     * Creates a new department.
     *
     * @param department the department object to be created
     * @return the created department object with its generated id
     */
    public Department createDepartment(Department department);

    /**
     * Updates the details of an existing department.
     *
     * @param id the unique identifier of the department to be updated
     * @param department the department object containing updated information
     * @return the updated department object
     * @throws RuntimeException if no department is found with the given id
     */
    public Department updateDepartment(Long id, Department department);

    /**
     * Deletes a department by its unique identifier.
     *
     * @param id the unique identifier of the department to be deleted
     * @throws RuntimeException if no department is found with the given id
     */
    public void deleteDepartment(Long id);
}
