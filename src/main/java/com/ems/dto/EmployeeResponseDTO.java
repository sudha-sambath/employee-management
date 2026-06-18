package com.ems.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeResponseDTO {

    private String name;
    private String department;
    private String email;
    private Double salary;
    private Integer age;
    private String jobTitle;

}
