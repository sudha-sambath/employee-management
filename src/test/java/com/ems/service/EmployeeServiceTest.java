package com.ems.service;

import com.ems.client.DepartmentClient;
import com.ems.config.ServiceConfig;
import com.ems.dto.DepartmentResponseDTO;
import com.ems.dto.EmployeeDetailsDTO;
import com.ems.dto.EmployeeResponseDTO;
import com.ems.dto.PayrollResponseDTO;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentClient departmentClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ServiceConfig serviceConfig;

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

    @Test
    void getEmployeesByNameAndDepartment() {

        List<Employee> employees = List.of(
                new Employee(4L,"Sarah","IT",90000.00)
        );

        when(employeeRepository.getEmployeesByNameAndDepartment("Sarah","IT"))
                .thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getEmployeesByNameAndDepartment("Sarah","IT");

        assertEquals(1, result.size());

        assertEquals("Sarah", result.get(0).getName());
        assertEquals("IT", result.get(0).getDepartment());

        verify(employeeRepository,times(1))
                .getEmployeesByNameAndDepartment("Sarah","IT");
    }

    @Test
    void delete() {
        Long employeeId = 1L;

        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        employeeService.delete(employeeId);

        verify(employeeRepository,times(1)).existsById(employeeId);
        verify(employeeRepository,times(1)).deleteById(employeeId);

    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound(){

        Long employeeId = 100L;

        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()-> {
            employeeService.delete(employeeId);
        });

        verify(employeeRepository,times(1)).existsById(employeeId);
        verify(employeeRepository, never()).deleteById(employeeId);
    }

    @Test
    void getAllEmployees() {
        List<Employee> employees = List.of(
                new Employee(null,"Sarah","IT",90000.00),
                new Employee(null,"Chris","IT", 100000.00)
        );

        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());

        assertEquals("Sarah", result.get(0).getName());
        assertEquals("IT", result.get(0).getDepartment());
        assertEquals("Chris", result.get(1).getName());
        assertEquals("IT", result.get(1).getDepartment());

        verify(employeeRepository,times(1)).findAll();
    }

    @Test
    void findById() {

        Long empId = 1L;

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setDepartmentId(10L);

        DepartmentResponseDTO departmentDTO =new DepartmentResponseDTO();
        departmentDTO.setId(10L);
        departmentDTO.setName("IT");

        PayrollResponseDTO payrollDTO = new PayrollResponseDTO();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

        when(departmentClient.getDepartmentById(10L)).thenReturn(departmentDTO);

        when(serviceConfig.getPayrollUrl()).thenReturn("http://payroll/api/");

        when(restTemplate.getForObject("http://payroll/api/" + empId,
                PayrollResponseDTO.class)).thenReturn(payrollDTO);

        EmployeeDetailsDTO result = employeeService.findById(empId);

        assertNotNull(result);
        assertNotNull(result.getDepartment());

        assertEquals("IT",result.getDepartment().getName());

        verify(employeeRepository,times(1)).findById(empId);
        verify(departmentClient,times(1)).getDepartmentById(10L);
        verify(restTemplate,times(1))
                .getForObject("http://payroll/api/"+ empId, PayrollResponseDTO.class);

    }

    @Test
    void shouldReturnEmployeeWithoutDepartment_WhenDepartmentIdIsNull(){

        Long empId = 1L;

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setDepartmentId(null);

        PayrollResponseDTO payrollDTO = new PayrollResponseDTO();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

        when(serviceConfig.getPayrollUrl()).thenReturn("http://payroll/api/");

        when(restTemplate.getForObject(anyString(),
                eq(PayrollResponseDTO.class))).thenReturn(payrollDTO);

        EmployeeDetailsDTO result = employeeService.findById(empId);

        assertNotNull(result);
        assertNull(result.getDepartment());

        verify(departmentClient,never()).getDepartmentById(anyLong());

    }

    // TC_01: Employee Found With Department and Payroll
    @Test
    void shouldThrowException_WhenEmployeeNotFound(){
        Long empId = 100L;

       when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
               () -> employeeService.findById(empId));

        assertEquals("Employee not found with id: 100", ex.getMessage());

        verify(employeeRepository, times(1)).findById(empId);

        verifyNoInteractions(departmentClient);
        verifyNoInteractions(restTemplate);

    }

    //TC_02: Employee Found but Department ID is Null
    @Test
    void findEmployeeById() {
        Long empId = 1L;

        Employee employee = new Employee(1L,"Sarah","IT",90000.00);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

       EmployeeResponseDTO result = employeeService.findEmployeeById(empId);

       assertNotNull(result);
       assertEquals("Sarah",result.getName());
       assertEquals("IT",result.getDepartment());

       verify(employeeRepository,times(1)).findById(1L);
    }

    //TC_03: Employee Not Found
    @Test
    void shouldThrowException_WhenEmployeeNotFoundForGivenID(){
        Long empId = 10L;

        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.findEmployeeById(empId));

        assertEquals("Employee Not found with Id"+empId, ex.getMessage());

        verify(employeeRepository,times(1)).findById(empId);

    }
}