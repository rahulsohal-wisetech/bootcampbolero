// department.ts
export class Department {
  id: number; // Required property
  name: string;
  mandatory: boolean;
  readOnly: boolean;

  constructor(id: number, name: string, mandatory: boolean, readOnly: boolean) {
    this.id = id;
    this.name = name;
    this.mandatory = mandatory;
    this.readOnly = readOnly;
  }
}
