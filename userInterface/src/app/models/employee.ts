// employee.ts
import {Department} from './department';

export class Employee {
  id: number;
  firstName: string;
  lastName: string;
  departments: Department[];

  constructor(id: number, firstName: string, lastName: string) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.departments = []; // Initialize here
  }
}
