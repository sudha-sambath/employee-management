package com.ems.service;

import com.ems.dto.EmployeeResponseDTO;
import com.ems.entity.Employee;
import com.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeByDept() {

        // Arrange

        String dept = "IT";
        Employee emp1 = new Employee(1L,"John","IT","john@example.com",25000.00,21, 1L,"Developer");
        Employee emp2 = new Employee(1L,"Alice","IT","alice@example.com",25000.00,21, 1L,"Developer");

        List<Employee> employeeList = Arrays.asList(emp1, emp2);

         when(employeeRepository.findByDepartment(dept))
                        .thenReturn(employeeList);

         // Act
        List<EmployeeResponseDTO> result = employeeService.getEmployeeByDept(dept);

       // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John",result.get(0).getName());
        assertEquals("Alice",result.get(1).getName());

        verify(employeeRepository,times(1))
                .findByDepartment(dept);
    }

    @Test
    void testFindEmployeeByDepartment_NoEmployeesFound(){

        String dept = "HR";

        when(employeeRepository.findByDepartment(dept))
                .thenReturn(Collections.emptyList());

        List<EmployeeResponseDTO> result = employeeService.getEmployeeByDept(dept);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeRepository).findByDepartment(dept);

    }

    @Test
    void getTop5HighestSalaryEmployees() {

        List<Employee> employees = List.of(
                new Employee("Chris", 100000.00),
                new Employee("Sarah", 90000.00),
                new Employee("David", 80000.00),
                new Employee("Sophia", 75000.00),
                new Employee("Mike", 70000.00)
        );

        when(employeeRepository.findTop5HighestSalaryEmployees())
                .thenReturn(employees);

       List<EmployeeResponseDTO> result = employeeService.getTop5HighestSalaryEmployees();

       assertThat(result)
               .hasSize(5)
               .extracting(EmployeeResponseDTO::getSalary)
               .containsExactly(
                       100000.00,
                       90000.00,
                       80000.00,
                       75000.00,
                       70000.00
               );
       verify(employeeRepository, times(1))
               .findTop5HighestSalaryEmployees();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist(){

        // Given
        when(employeeRepository.findTop5HighestSalaryEmployees())
                .thenReturn(Collections.emptyList());

        // When
        List<EmployeeResponseDTO> result = employeeService.getTop5HighestSalaryEmployees();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeRepository, times(1)).findTop5HighestSalaryEmployees();
    }
}