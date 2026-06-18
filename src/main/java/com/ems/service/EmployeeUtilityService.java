package com.ems.service;

import com.ems.dto.EmployeeResponseDTO;
import com.ems.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeUtilityService {

    public List<Employee> getHighEarners(List<Employee> employeeList){
        return employeeList.stream()
                .filter(e-> e.getSalary() > 50000)
                .toList();
    }

//    7. Count employees in each department
    public Map<String,Long> deptEmployeeCount(List<Employee> employeeList){
        return employeeList.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.counting()
        ));
    }

    public Employee maxSalaryEmp(List<Employee> employeeList){
        return employeeList.stream()
//                .collect(Collectors.averagingDouble(Employee::getSalary));
                .max(Comparator.comparing(Employee::getSalary))
                .get();
                
    }

    public Map<String,List<Employee>> groupByDept(List<Employee> employeeList){
        return employeeList.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));


    }

}

