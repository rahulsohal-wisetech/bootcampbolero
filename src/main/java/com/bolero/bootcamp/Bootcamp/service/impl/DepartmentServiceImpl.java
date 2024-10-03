package com.bolero.bootcamp.Bootcamp.service.impl;

import com.bolero.bootcamp.Bootcamp.entity.Department;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidDepartmentException;
import com.bolero.bootcamp.Bootcamp.repository.DepartmentRepository;
import com.bolero.bootcamp.Bootcamp.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        log.info("Fetching department with ID: {}", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", id);
                    return new DepartmentNotFoundException("Department not found with ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Department> getAllDepartments(Pageable pageable) {
        log.info("Fetching all departments with pagination");
        return departmentRepository.findAll(pageable);
    }

    @Transactional
    public Department createDepartment(Department department) {
        log.info("Creating new department: {}", department.getName());
        if (department.getName() == null || department.getName().isEmpty()) {
            log.error("Department creation failed due to invalid name");
            throw new InvalidDepartmentException("Department name cannot be null or empty");
        }
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        log.info("Updating department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", id);
                    return new DepartmentNotFoundException("Department not found with ID: " + id);
                });
        if (department.getName() == null || department.getName().isEmpty()) {
            log.error("Department name cannot be null or empty");
            throw new InvalidDepartmentException("Department name cannot be null or empty");
        }

        if (department.isReadOnly()) {
            log.error("Attempted to update a read-only department: {}", department.getName());
            throw new IllegalArgumentException("Cannot update a read-only department");
        }

        department.setName(departmentDetails.getName());
        department.setDefault(departmentDetails.isDefault());
        log.info("Successfully updated department: {}", department.getName());
        return departmentRepository.save(department);
    }


    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", id);
                    return new DepartmentNotFoundException("Department not found with ID: " + id);
                });
        if (department != null) {

            if (department.isReadOnly()) {
                log.error("Attempted to delete a read-only department: {}", department.getName());
                throw new IllegalArgumentException("Cannot delete a read-only department");
            }

            departmentRepository.delete(department);
            log.info("Successfully deleted department with ID: {}", id);
        }
    }

}
