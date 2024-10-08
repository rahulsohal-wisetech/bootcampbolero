import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import { Department } from '../models/department';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private apiServerUrl = 'http://localhost:8080/api/v1/departments'; // Spring Boot API

  constructor(private http: HttpClient) {}

  public getDepartments(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiServerUrl}?page=${page}&size=${size}`);
  }

  public addDepartment(department: Department): Observable<Department> {
    return this.http.post<Department>(`${this.apiServerUrl}`, department);
  }

  public updateDepartment(department: Department): Observable<Department> {
    return this.http.put<Department>(`${this.apiServerUrl}/${department.id}`, department);
  }

  // Delete an department
  public deleteDepartment(departmentId: number): Observable<void> {
    return this.http.delete<void>(`/api/departments/${departmentId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unknown error occurred!';
        if (error.status === 403) {
          errorMessage = 'Cannot delete a read-only department!';
        }
        return throwError(() => errorMessage);
      })
    );
  }
}
