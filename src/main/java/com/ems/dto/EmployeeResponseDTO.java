package com.ems.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private String name;
    private String department;
    private String email;
    private Double salary;
    private Integer age;
    private String jobTitle;

    public EmployeeResponseDTO(String name, Double salary, String department){
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

}
