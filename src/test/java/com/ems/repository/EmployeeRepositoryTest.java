package com.ems.repository;

import com.ems.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    void findByDepartment() {

        String department = "Product Engineering";

        Employee employee1 = new Employee();
        employee1.setName("John");
        employee1.setDepartment("Product Engineering");
        employee1.setEmail("john@example.com");
        employee1.setSalary(75000.00);
        employee1.setAge(23);
        employee1.setJobTitle("Software Engineer");

        Employee employee2 = new Employee();
        employee2.setName("sham");
        employee2.setDepartment("Product Engineering");
        employee2.setEmail("sham@example.com");
        employee2.setSalary(75000.00);
        employee2.setAge(23);
        employee2.setJobTitle("Software Engineer");

        Employee employee3 = new Employee();
        employee3.setName("Manju");
        employee3.setDepartment("Finance");
        employee3.setEmail("manju@example.com");
        employee3.setSalary(75000.00);
        employee3.setAge(23);
        employee3.setJobTitle("Accountant");

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);

        List<Employee> result = employeeRepository.findByDepartment(department);

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(employee -> department.equals(employee.getDepartment())));


    }

    @Test
    void shouldReturnEmptyListWhenDepartmentNotFound() {
        // Act
        List<Employee> result =
                employeeRepository.findByDepartment("Finance");

        // Assert
        assertTrue(result.isEmpty());
    }

    /*
    These four tests cover:

    ✅ Fewer than 5 records
    ✅ Exactly 5 records
    ✅ Duplicate salaries with more than 5 records
    ✅ Empty table
     */
    @Test
    void findTop5HighestSalaryEmployees() {

       employeeRepository.saveAll(List.of(
               new Employee("John",50000.00),
               new Employee("David", 80000.00),
               new Employee("Mike", 70000.00),
               new Employee("Sarah", 90000.00),
               new Employee("Emma", 60000.00),
               new Employee("Chris", 100000.00),
               new Employee("Sophia", 75000.00)
       ));

       List<Employee> result = employeeRepository.findTop5HighestSalaryEmployees();

       assertEquals(5, result.size());

        assertEquals(100000.00, result.get(0).getSalary());
        assertEquals(90000.00, result.get(1).getSalary());
        assertEquals(80000.00, result.get(2).getSalary());
        assertEquals(75000.00, result.get(3).getSalary());
        assertEquals(70000.00, result.get(4).getSalary());

        // Verify descending order
        for(int i = 0; i < result.size() - 1; i++){
            assertTrue(result.get(i).getSalary() >= result.get(i + 1).getSalary(),
                    "Salaries should be sorted in descending order");
        }
    }

    @Test
    void shouldReturnAllEmployeesWhenLessThanFiveExists(){
        employeeRepository.saveAll(List.of(
                new Employee("John",50000.00),
                new Employee("David", 80000.00),
                new Employee("Mike", 70000.00)
        ));

        List<Employee> result =  employeeRepository.findTop5HighestSalaryEmployees();

        assertEquals(3, result.size());

        assertEquals(80000.00, result.get(0).getSalary());
        assertEquals(70000.00, result.get(1).getSalary());
        assertEquals(50000.00, result.get(2).getSalary());

    }

    @Test
    void shouldReturnOnlyFiveEmployeesWhenMultipleHaveSameSalary(){
        employeeRepository.saveAll(List.of(
                new Employee("Emp1", 100000.00),
                new Employee("Emp2", 100000.00),
                new Employee("Emp3", 90000.00),
                new Employee("Emp4", 90000.00),
                new Employee("Emp5", 80000.00),
                new Employee("Emp6", 70000.00),
                new Employee("Emp7", 60000.00)
        ));

        // When
        List<Employee> result = employeeRepository.findTop5HighestSalaryEmployees();
        assertThat(result)
                .hasSize(5)
                .extracting(Employee::getSalary)
                .containsExactly(
                        100000.00,
                        100000.00,
                        90000.00,
                        90000.00,
                        80000.00
                );

        assertEquals(80000.00, result.get(4).getSalary());

    }

    @Test
    void shouldReturnAllEmployeesWhenExactlyFiveExists(){
        List<Employee> employees =  employeeRepository.saveAll(List.of(
                new Employee("John",50000.00),
                new Employee("David", 80000.00),
                new Employee("Mike", 70000.00),
                new Employee("Sarah", 90000.00),
                new Employee("Emma", 60000.00)
        ));
        List<Employee> result = employeeRepository.findTop5HighestSalaryEmployees();

        assertThat(result)
                .hasSize(5)
                .extracting(Employee::getSalary)
                .containsExactly(
                        90000.00,
                        80000.00,
                        70000.00,
                        60000.00,
                        50000.00
                );

    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist(){

        List<Employee> result =  employeeRepository.findTop5HighestSalaryEmployees();

        assertNotNull(result);
        assertTrue(result.isEmpty());

    }
}