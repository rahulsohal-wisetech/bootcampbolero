import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { Department } from '../models/department';
import {response} from 'express';  // Create a model for department

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private apiServerUrl = 'http://localhost:8080/api/v1/departments'; // Spring Boot API

  constructor(private http: HttpClient) {}

  // Get all departments
  public getDepartments(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiServerUrl}?page=${page}&size=${size}`);
  }

  // Add an department
  public addDepartment(department: Department): Observable<Department> {
    return this.http.post<Department>(`${this.apiServerUrl}`, department);
  }

  // Update an department
  public updateDepartment(department: Department): Observable<Department> {
    return this.http.put<Department>(`${this.apiServerUrl}/${department.id}`, department);
  }

  // Delete an department
  public deleteDepartment(departmentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/${departmentId}`);
  }
}
