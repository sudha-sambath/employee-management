package com.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailsDTO {
    private EmployeeResponseDTO employee;
    private DepartmentResponseDTO department;
    private PayrollResponseDTO payroll;
}
