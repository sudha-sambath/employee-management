package com.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {

    @NotBlank(message="Name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    @Email(message = "Invalid email format")
    @NotBlank(message="Email is required")
    private String email;

    @NotNull(message = "Salary must not be null")
    @Min(value = 1000, message = "Salary must be at least 1000")
    private Double salary;

    @NotNull(message = "Age must not be null")
    @Min(value = 18, message = "Age must be greater than 18")
    private int age;

    @NotBlank(message = "jobTitle is required")
    private String jobTitle;

}
