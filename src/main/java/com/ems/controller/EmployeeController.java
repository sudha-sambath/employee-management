package com.ems.controller;

import com.ems.dto.*;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployee(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDetailsDTO getById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(@Valid  @RequestBody EmployeeRequestDTO dto){
        EmployeeResponseDTO response = employeeService.saveEmployee(dto);
       return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id){
            employeeService.delete(id);
            return ResponseEntity.ok("Employee deleted successfully");
    }

    @GetMapping("/by-dept")
    public List<EmployeeResponseDTO> getEmployeeByDept(@RequestParam  @Pattern(regexp = "HR|IT|Finance", message="Invalid Department")
                                                                   String department) {
        return employeeService.getEmployeeByDept(department);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id,
                                                              @RequestBody EmployeeRequestDTO dto){
        EmployeeResponseDTO response =  employeeService.updateEmployee(id,dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> partialUpdate(@PathVariable Long id,
                                                             @RequestBody Map<String,Object> updates){
        EmployeeResponseDTO response = employeeService.partialUpdate(id, updates);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public List<EmployeeResponseDTO> searchEmployees(@Valid EmployeeSearchDTO searchDTO){
        return employeeService.search(searchDTO);
    }

    @GetMapping("/byNameAndDept")
    public List<EmployeeResponseDTO> getEmployeesByNameAndDepartment(@RequestParam String name,
                                                                     @RequestParam String department){
        return employeeService.getEmployeesByNameAndDepartment(name,department);
    }

    @GetMapping("/high-salary")
    public List<EmployeeResponseDTO> getTop5HighestSalaryEmployees(){
        return employeeService.getTop5HighestSalaryEmployees();
    }

    /* Pagination + Sorting + dynamic filtering*/
    @GetMapping("/page")
    public PageResponse<EmployeeResponseDTO> getPageableEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "asc")  String sortDir){

        EmployeeFilter filter = new EmployeeFilter();
        filter.setName(name);
        filter.setDepartment(department);
        filter.setMinSalary(minSalary);
        filter.setMaxSalary(maxSalary);

        return employeeService.getPageableEmployees(filter,pageNo,pageSize,sortBy,sortDir);
    }

    @GetMapping("/findById/{id}")
    public EmployeeResponseDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.findEmployeeById(id);
    }

}
