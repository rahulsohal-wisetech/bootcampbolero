import { Component, OnInit } from '@angular/core';
import { Employee } from './models/employee';
import { Department } from './models/department';
import { EmployeeService } from './employees/employee.service';
import { DepartmentService } from './departments/department.service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import {CommonModule} from '@angular/common';
import $ from 'jquery';

declare var window: any; // For Bootstrap Modal

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [EmployeeService, DepartmentService],  // Ensure services are provided
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  employees: Employee[] = [];
  departments: Department[] = [];
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  employeeForm: Employee= new Employee(0, '', '');
  departmentForm: Department = new Department(0, '', false, false);
  selectedDepartment: Department | null = null;
  availableDepartments: Department[] = [];

  isEditEmployee: boolean = false;
  isEditDepartment: boolean = false;

  employeeModal: any;
  departmentModal: any;
  assignDepartmentModal: any;

  errorMessage: string | null = null;
  warningMessage: string | null = null;

  constructor(
    private employeeService: EmployeeService,
    private departmentService: DepartmentService
  ) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined' && window.bootstrap) {
      this.employeeModal = new window.bootstrap.Modal(document.getElementById('employeeModal'));
      this.departmentModal = new window.bootstrap.Modal(document.getElementById('departmentModal'));
      this.assignDepartmentModal = new window.bootstrap.Modal(document.getElementById('assignDepartmentModal'))
    }
    this.fetchEmployees();
    this.fetchDepartments();
  }


  fetchEmployees() {
    this.employeeService.getEmployees(this.currentPage, this.pageSize).subscribe(response => {
      this.employees = response.content;
      this.totalPages = response.totalPages;
    });
  }

  fetchDepartments() {
    this.departmentService.getDepartments(this.currentPage, this.pageSize).subscribe(response => {
      this.departments = response.content;  // Extract array
      this.totalPages = response.totalPages;
    });
  }

  openAssignDepartmentModal(employee: Employee): void {
    this.selectedDepartment = null;
    this.employeeForm = employee;
    this.availableDepartments = this.departments.filter(
      (dept) => !employee.departments.some((assignedDept) => assignedDept.id === dept.id)
    );
    this.assignDepartmentModal.show();
  }

  isAssigned(employeeId: number, departmentId: number): boolean {
    return this.employees.find(employee => employee.id === employeeId)?.departments.some(department => department.id === departmentId) || false;
  }

  assignOrUnassignDepartment(): void {
    if (this.selectedDepartment && this.employeeForm) {
      const isAssigned = this.employeeForm.departments.includes(this.selectedDepartment);

      if (isAssigned) {
        this.unassignDepartment(this.selectedDepartment, this.employeeForm);
      } else {
        this.assignDepartment(this.selectedDepartment, this.employeeForm);
      }

      // Hide the modal after the operation is done
      const modalElement = $('#assignDepartmentModal') as any;
      if (modalElement) {
        modalElement.modal('hide');
      }
    }
  }

  assignDepartment(department: Department, employee: Employee): void {
    this.employeeService.assignDepartmentToEmployee(employee.id, department.id).subscribe(() => {
      console.log(`Department ${department.name} assigned to employee ${employee.firstName}`);
      employee.departments.push(department); // Add department to employee's assigned list
      this.availableDepartments = this.availableDepartments.filter(dept => dept.id !== department.id); // Remove from available
    },
      (error) => {
        this.errorMessage = `Failed to assign department: ${error.message}`;
      });
  }

  // Unassign department from employee
  unassignDepartment(department: Department, employee: Employee): void {
    this.employeeService.unassignDepartmentFromEmployee(employee.id, department.id).subscribe(() => {
      console.log(`Department ${department.name} unassigned from employee ${employee.firstName}`);
      employee.departments = employee.departments.filter(dept => dept.id !== department.id); // Remove from employee's departments
      this.availableDepartments.push(department); // Add back to available departments
    },
      (error) => {
        this.errorMessage = `Failed to unassign department: ${error.message}`;
      });
  }

  openEmployeeModal(employee?: Employee) {
    this.isEditEmployee = !!employee;
    this.employeeForm = employee ? { ...employee } : new Employee(0, '', '');
    this.employeeModal.show();
  }

  openDepartmentModal(department?: Department) {
    this.isEditDepartment = !!department;
    this.departmentForm = department ? { ...department } : new Department(0, '', false, false);
    this.departmentModal.show();
  }

  saveEmployee() {
    if (this.isEditEmployee) {
      this.employeeService.updateEmployee(this.employeeForm).subscribe(() => {
        this.fetchEmployees();
      },
        (error) => {
          this.errorMessage = `Failed to update employee: ${error.message}`;
        });
    } else {
      this.employeeService.addEmployee(this.employeeForm).subscribe(() => {
          this.fetchEmployees();
      },
        (error) => {
          this.errorMessage = `Failed to create department: ${error.message}`;
        });
    }
    this.employeeModal.hide();
  }

  saveDepartment() {
    if (this.isEditDepartment) {
      this.departmentService.updateDepartment(this.departmentForm).subscribe(() => {
        this.fetchDepartments();
      },
        (error) => {
          this.errorMessage = `Failed to update department: ${error.message}`;
      });
    } else {
      this.departmentService.addDepartment(this.departmentForm).subscribe(() => {
        this.fetchDepartments();
      },
        (error) => {
          this.errorMessage = `Failed to create department: ${error.message}`;
      });
    }
    this.departmentModal.hide();
  }

  deleteEmployee(employeeId: number) {
    this.employeeService.deleteEmployee(employeeId).subscribe(() => {
      this.fetchEmployees();
    });
  }

  deleteDepartment(departmentId: number) {
    this.departmentService.deleteDepartment(departmentId).subscribe(() => {
      this.fetchDepartments();
    },
      (error) => {
        this.errorMessage = `Failed to delete department: ${error.message}`;
      });
  }

  closeAlert(): void {
    this.errorMessage = null;
    this.warningMessage = null;
  }
}
