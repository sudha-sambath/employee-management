package com.ems.service;

import com.ems.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EmployeeUtilityServiceTest {


    private EmployeeUtilityService service;
    private List<Employee> employees;


    @BeforeEach
    void setUp(){

        service = new EmployeeUtilityService();

        employees = List.of(
                new Employee(1L, "John", "IT", 40000.0),
                new Employee(2L, "Mary", "HR", 60000.0),
                new Employee(3L, "David", "IT", 80000.0),
                new Employee(4L, "Sarah", "Finance", 55000.0)
        );

    }

    @Test
    void getHighEarners() {
        List<Employee> result = service.getHighEarners(employees);

        assertEquals(3, result.size());

        assertTrue(result.stream().allMatch(emp -> emp.getSalary() > 50000));
        assertTrue(result.stream().anyMatch(emp -> emp.getName().equals("Sarah")));
        assertTrue(result.stream().anyMatch(emp -> emp.getName().equals("Mary")));
        assertTrue(result.stream().anyMatch(emp -> emp.getName().equals("David")));
    }

    @Test
    void deptEmployeeCount() {
        Map<String, Long> result = service.deptEmployeeCount(employees);

        assertEquals(2L, result.get("IT"));
        assertEquals(1L, result.get("HR"));
        assertEquals(1L, result.get("Finance"));
    }

    @Test
    void maxSalaryEmp() {
        Employee result = service.maxSalaryEmp(employees);

        assertNotNull(result);
        assertEquals("David", result.getName());
        assertEquals(80000.0, result.getSalary());

    }

    @Test
    void groupByDept() {
        Map<String, List<Employee>> result = service.groupByDept(employees);

        assertEquals(3, result.size());

        assertEquals(2, result.get("IT").size());
        assertEquals(1, result.get("HR").size());
        assertEquals(1, result.get("Finance").size());
    }
}