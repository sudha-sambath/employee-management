package com.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFilter {
    private String name;
    private String department;
    private Double minSalary;
    private Double maxSalary;
}
