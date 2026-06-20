package com.ems.controller;

import com.ems.dto.EmployeeResponseDTO;
import com.ems.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;


    @Test
    void getEmployeeByDept() throws Exception {

        EmployeeResponseDTO emp1 = new EmployeeResponseDTO();
        emp1.setName("John");
        emp1.setDepartment("IT");
        emp1.setEmail("john@example.com");
        emp1.setSalary(25000.00);
        emp1.setAge(21);
        emp1.setJobTitle("Developer");

        EmployeeResponseDTO emp2 = new EmployeeResponseDTO();
        emp2.setName("Alice");
        emp2.setDepartment("IT");
        emp2.setEmail("alice@example.com");
        emp2.setSalary(25000.00);
        emp2.setAge(21);
        emp2.setJobTitle("Developer");

        List<EmployeeResponseDTO> result = Arrays.asList(emp1, emp2);

        when(employeeService.getEmployeeByDept("IT"))
                .thenReturn((result));

        mockMvc.perform(get("/employees/by-dept")
                .param("department", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].department").value("IT"))
                 .andExpect(jsonPath("$[1].name").value("Alice"))
                 .andExpect(jsonPath("$[1].department").value("IT"));

        verify(employeeService).getEmployeeByDept("IT");
    }

    @Test
    void shouldReturnBadRequestForInvalidDepartment() throws Exception {

        mockMvc.perform(get("/employees/by-dept")
                        .param("department", "Sales"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTop5HighestSalaryEmployees() throws Exception {

        // Given
        List<EmployeeResponseDTO> employees = List.of(
                new EmployeeResponseDTO("Chris", 100000.00),
                new EmployeeResponseDTO("Sarah", 90000.00),
                new EmployeeResponseDTO("David", 80000.00),
                new EmployeeResponseDTO("Sophia", 75000.00),
                new EmployeeResponseDTO("Mike", 70000.00)
        );

        when(employeeService.getTop5HighestSalaryEmployees()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/employees/high-salary"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Chris"))
                .andExpect(jsonPath("$[0].salary").value(100000.00))
                .andExpect(jsonPath("$[1].name").value("Sarah"))
                .andExpect(jsonPath("$[1].salary").value(90000.00));

        verify(employeeService, times(1)).getTop5HighestSalaryEmployees();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() throws Exception {
        when(employeeService.getTop5HighestSalaryEmployees())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/employees/high-salary"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(employeeService, times(1)).getTop5HighestSalaryEmployees();

    }
}