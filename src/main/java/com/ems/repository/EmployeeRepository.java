package com.ems.repository;

import com.ems.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long>, JpaSpecificationExecutor<Employee> {
     // Derived queries → simple filters → uses method name
     List<Employee> findByDepartment(String department);

     /* JPQL → multiple conditions,joins →
     Entity-based queries(More flexible than derived queries)*/
    @Query("SELECT e FROM Employee e WHERE e.name=:name AND e.department=:department")
    List<Employee> getEmployeesByNameAndDepartment(@Param("name")String name, @Param("department")String department);

   /*Native SQL → complex joins or optimization,Database-specific functions,Reporting / analytics queries
   → actual SQL queries
    */
    @Query(value="SELECT * FROM employee ORDER BY salary DESC LIMIT 5" , nativeQuery = true)
    List<Employee> findTop5HighestSalaryEmployees();

    @Transactional
    void deleteByDepartment(String department);

}
