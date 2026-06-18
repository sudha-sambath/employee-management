package com.ems.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollResponseDTO {
    private Long employeeId;
    private Double salary;
    private Double bonus;
    private Double tax;
    private LocalDate paymentDate;
}
