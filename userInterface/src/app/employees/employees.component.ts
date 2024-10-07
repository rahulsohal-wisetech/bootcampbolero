import {Component, OnInit} from '@angular/core';
import {Employee} from '../models/employee';
import {EmployeeService} from './employee.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent implements OnInit {

  employees: Employee[] = [];

  constructor(private employeeService: EmployeeService) { }

  ngOnInit(): void {
    this.employeeService.getEmployees().subscribe(
      (response: Employee[]) => {
        this.employees = response;
      },
      (error) => {
        console.error('Error fetching employees:', error);
      }
    );
  }
}
