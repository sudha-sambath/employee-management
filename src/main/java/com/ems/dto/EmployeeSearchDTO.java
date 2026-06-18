package com.ems.dto;

/* This searchDTO used for validating GetMapping parameters.
@Min,@Max,@Size,@Pattern this validation annotation are used in GetMapping parameters.
 It throws ConstraintViolationException */

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchDTO {

    @Size(min=2,message = "Name must contain at least 2 characters")
    public String name;

    @Pattern(regexp = "HR|IT|Finance", message="Invalid Department")
    public String department;

    @Min(value = 1000, message = "MinimumSalary must be greater than 1000")
    public Integer minSalary;

}
