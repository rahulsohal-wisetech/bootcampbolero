CREATE SEQUENCE SEQ_EMPLOYEE_ID
    START WITH 1
    INCREMENT BY 1;


CREATE SEQUENCE SEQ_DEPARTMENT_ID
    START WITH 1
    INCREMENT BY 1;


CREATE TABLE EMPLOYEE (
    ID BIGINT DEFAULT NEXT VALUE FOR SEQ_EMPLOYEE_ID PRIMARY KEY,
    FIRST_NAME VARCHAR(100) NOT NULL,
    LAST_NAME VARCHAR(100) NOT NULL
);


CREATE TABLE DEPARTMENT (
    ID BIGINT DEFAULT NEXT VALUE FOR SEQ_DEPARTMENT_ID PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    MANDATORY BOOLEAN NOT NULL,
    READ_ONLY BOOLEAN NOT NULL
);


CREATE TABLE EMPLOYEE_DEPARTMENT_MAP (
    EMPLOYEE_ID BIGINT NOT NULL,
    DEPARTMENT_ID BIGINT NOT NULL,
    PRIMARY KEY (EMPLOYEE_ID, DEPARTMENT_ID),
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE(ID) ON DELETE CASCADE,
    FOREIGN KEY (DEPARTMENT_ID) REFERENCES DEPARTMENT(ID) ON DELETE CASCADE
);

