import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { Employee } from '../models/employee';
import {response} from 'express';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiServerUrl = 'http://localhost:8080/api/v1/employees'; // Spring Boot API

  constructor(private http: HttpClient) {}

  // Get all employees
  public getEmployees(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiServerUrl}?page=${page}&size=${size}`);
  }

  // Add an employee
  public addEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiServerUrl}`, employee);
  }

  // Update an employee
  public updateEmployee(employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiServerUrl}/${employee.id}`, employee);
  }

  // Delete an employee
  public deleteEmployee(employeeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/${employeeId}`);
  }

  assignDepartmentToEmployee(employeeId: number, departmentId: number): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiServerUrl}/${employeeId}/departments/${departmentId}`, {});
  }

  unassignDepartmentFromEmployee(employeeId: number, departmentId: number): Observable<Employee> {
    return this.http.delete<Employee>(`${this.apiServerUrl}/${employeeId}/departments/${departmentId}`);
  }
}
