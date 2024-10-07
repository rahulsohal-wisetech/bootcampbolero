import { Component, OnInit } from '@angular/core';
import { Employee } from './models/employee';
import { Department } from './models/department';
import { EmployeeService } from './employees/employee.service';
import { DepartmentService } from './departments/department.service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import {CommonModule} from '@angular/common';

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

  isEditEmployee: boolean = false;
  isEditDepartment: boolean = false;

  employeeModal: any;
  departmentModal: any;

  constructor(
    private employeeService: EmployeeService,
    private departmentService: DepartmentService
  ) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined' && window.bootstrap) {
      this.employeeModal = new window.bootstrap.Modal(document.getElementById('employeeModal'));
      this.departmentModal = new window.bootstrap.Modal(document.getElementById('departmentModal'));
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

  fetchDepartments(page: number=0, size: number=10) {
    this.departmentService.getDepartments(this.currentPage, this.pageSize).subscribe(response => {
      this.departments = response.content;  // Extract array
      this.totalPages = response.totalPages;
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
      });
    } else {
      this.employeeService.addEmployee(this.employeeForm).subscribe(() => {
        this.fetchEmployees();
      });
    }
    this.employeeModal.hide();
  }

  saveDepartment() {
    if (this.isEditDepartment) {
      this.departmentService.updateDepartment(this.departmentForm).subscribe(() => {
        this.fetchDepartments();
      });
    } else {
      this.departmentService.addDepartment(this.departmentForm).subscribe(() => {
        this.fetchDepartments();
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
    });
  }
}
