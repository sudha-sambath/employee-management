package com.ems.controller;

import com.ems.dto.DepartmentResponseDTO;
import com.ems.dto.EmployeeDetailsDTO;
import com.ems.dto.EmployeeResponseDTO;
import com.ems.dto.PayrollResponseDTO;
import com.ems.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                new EmployeeResponseDTO("Chris", 100000.00, "IT"),
                new EmployeeResponseDTO("Sarah", 90000.00,"IT"),
                new EmployeeResponseDTO("David", 80000.00,"HR"),
                new EmployeeResponseDTO("Sophia", 75000.00,"HR"),
                new EmployeeResponseDTO("Mike", 70000.00,"Finance")
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

    @Test
    void getEmployeesByNameAndDepartment() throws Exception {
        // Given
        List<EmployeeResponseDTO> employees = List.of(
                new EmployeeResponseDTO("Chris", 100000.00, "IT"),
                new EmployeeResponseDTO("Sarah", 90000.00,"IT"),
                new EmployeeResponseDTO("David", 80000.00,"HR"),
                new EmployeeResponseDTO("Sophia", 75000.00,"HR"),
                new EmployeeResponseDTO("Mike", 70000.00,"Finance")
        );

        when(employeeService.getEmployeesByNameAndDepartment("Sarah","IT"))
                .thenReturn(employees);

        mockMvc.perform(get("/employees/byNameAndDept")
                .param("name","Sarah")
                .param("department","IT"))
                .andExpect(status().isOk());

        verify(employeeService,times(1))
                .getEmployeesByNameAndDepartment("Sarah","IT");

    }

    @Test
    void deleteEmployee() throws Exception {
        Long employeeId = 1L;

        doNothing().when(employeeService).delete(employeeId);

        mockMvc.perform(delete("/employees/{id}",employeeId))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));

        verify(employeeService,times(1)).delete(employeeId);
    }

    @Test
    void deleteEmployee_shouldReturnNotFound() throws Exception{
        Long employeeId = 100L;

        doThrow(new ResourceNotFoundException(
                "Employee Not Found with ID:" + employeeId))
                .when(employeeService).delete(employeeId);

        mockMvc.perform(delete("/employees/{id}",employeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById() throws Exception {
        Long empId = 1L;
        EmployeeResponseDTO employee = new EmployeeResponseDTO();
        employee.setId(1L);
        employee.setName("John");

        DepartmentResponseDTO department = new DepartmentResponseDTO(10L,"IT","Dublin",1L);
        PayrollResponseDTO payroll = new PayrollResponseDTO();
        payroll.setEmployeeId(1L);
        payroll.setSalary(100000.00);

        EmployeeDetailsDTO employeeDetailsDTO = new EmployeeDetailsDTO();
        employeeDetailsDTO.setEmployee(employee);
        employeeDetailsDTO.setDepartment(department);
        employeeDetailsDTO.setPayroll(payroll);

        when(employeeService.findById(empId)).thenReturn(employeeDetailsDTO);

        mockMvc.perform(get("/employees/{id}",empId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.id").value(1))
                .andExpect(jsonPath("$.employee.name").value(employeeDetailsDTO.getEmployee().getName()))
                .andExpect(jsonPath("$.department.name").value(employeeDetailsDTO.getDepartment().getName()))
                .andExpect(jsonPath("$.payroll.salary").value(employeeDetailsDTO.getPayroll().getSalary()));

        verify(employeeService,times(1)).findById(1L);
    }

    @Test
    void getAllEmployee() throws Exception {
        List<EmployeeResponseDTO> employees = List.of(
                new EmployeeResponseDTO("Sarah",100000.0,"IT"));

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Sarah"))
                .andExpect(jsonPath("$[0].salary").value(100000.0))
                .andExpect(jsonPath("$[0].department").value("IT"));


        verify(employeeService, times(1)).getAllEmployees();
    }


    @Test
    void getEmployeeById() throws Exception {
        Long empId = 1L;

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setName("Sarah");

        when(employeeService.findEmployeeById(empId)).thenReturn(employeeResponseDTO);

        mockMvc.perform(get("/employees/findById/{id}",empId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sarah"));

        verify(employeeService,times(1)).findEmployeeById(1L);
    }

}