package com.ems.service;

import com.ems.client.DepartmentClient;
import com.ems.config.ServiceConfig;
import com.ems.dto.*;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;
    @Autowired
    private DepartmentClient departmentClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceConfig config;

    @Value("${services.payrollUrl}")
    private String url;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<EmployeeResponseDTO>  getAllEmployees(){

        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeDetailsDTO findById(Long id){

        Employee emp = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
        DepartmentResponseDTO department = null;

        if (emp.getDepartmentId() != null) {

            System.out.println("Employee departmentId: " + emp.getDepartmentId());
            System.out.println("Calling Department Service...");
            department = departmentClient.getDepartmentById(emp.getDepartmentId());
        }

        System.out.println("Department received: " + department.getName());

        EmployeeResponseDTO employeeResponseDTO = mapToResponse(emp);
        System.out.println(url);

        PayrollResponseDTO payrollResponseDTO = restTemplate.getForObject(url + id, PayrollResponseDTO.class);

        return new EmployeeDetailsDTO(employeeResponseDTO, department,payrollResponseDTO);
    }

    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto){
        Employee emp = mapToEntity(dto);
        Employee saved = repo.save(emp);

        return mapToResponse(saved);
    }

    public void delete(Long id){
        if(!repo.existsById(id)){
            throw new ResourceNotFoundException("Employee Not Found with ID:" + id);
        }

        repo.deleteById(id);
    }

    public List<EmployeeResponseDTO> getEmployeeByDept(String department){
         return repo.findByDepartment(department)
                 .stream()
                 .map(this::mapToResponse)
                 .toList();

    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto){
              Employee employee = repo.findById(id)
                      .orElseThrow(()->new ResourceNotFoundException("Employee Not Found"));

              employee.setName(dto.getName());
              employee.setDepartment(dto.getDepartment());
              employee.setEmail(dto.getEmail());
              employee.setSalary(dto.getSalary());
              employee.setJobTitle(dto.getJobTitle());
              employee.setAge(dto.getAge());

              repo.save(employee);

             return mapToResponse(employee);
    }

    public EmployeeResponseDTO partialUpdate(Long id, Map<String,Object> updates){
            Employee employee = repo.findById(id)
                    .orElseThrow(()->new ResourceNotFoundException("Employee not found"));

            if(updates.containsKey("name")){
                employee.setName((String) updates.get("name"));
            }
            if(updates.containsKey("department")){
                employee.setDepartment((String) updates.get("department"));
            }

            repo.save(employee);

            return mapToResponse(employee);
    }

    public List<EmployeeResponseDTO> search(EmployeeSearchDTO searchDTO){
        List<Employee> employee = repo.findAll(EmployeeSpecificationService.searchByCriteria(searchDTO));

        return employee.stream()
                .map(this::mapToResponse)
               .toList();
    }

    /* Mapping methods*/

    private Employee mapToEntity(EmployeeRequestDTO dto){
        Employee emp = new Employee();
        emp.setName(dto.getName());
        emp.setDepartment(dto.getDepartment());
        emp.setEmail(dto.getEmail());
        emp.setSalary(dto.getSalary());
        emp.setAge(dto.getAge());
        emp.setJobTitle(dto.getJobTitle());

        return emp;

    }

    private EmployeeResponseDTO mapToResponse(Employee employee){
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setName(employee.getName());
        dto.setDepartment(Optional.ofNullable(employee.getDepartment()).orElse("Unknown"));
        dto.setEmail(employee.getEmail());
        dto.setSalary(employee.getSalary() != null ? employee.getSalary() : 0);
        dto.setAge(employee.getAge() != null ? employee.getAge() : 0);
        dto.setJobTitle(employee.getJobTitle());
        return dto;

    }

    public List<EmployeeResponseDTO> getEmployeesByNameAndDepartment(String name, String department){
        List<Employee> employee = repo.getEmployeesByNameAndDepartment(name, department);
        return employee.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<EmployeeResponseDTO> getTop5HighestSalaryEmployees(){
        List<Employee> employee = repo.findTop5HighestSalaryEmployees();
        return employee.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PageResponse<EmployeeResponseDTO> getPageableEmployees(EmployeeFilter filter, int pageNo, int pageSize,
                                                          String sortBy, String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        //dynamic filtering
        Specification<Employee> specification = EmployeeSpecificationService.filter(filter);

        Page<Employee> employeePageable = repo.findAll(specification, pageable);

       List<EmployeeResponseDTO> employeeDTOList = employeePageable.getContent()
               .stream()
               .map(this::mapToResponse)
               .toList();

       return new PageResponse<>(employeeDTOList,
               employeePageable.getNumber(),
               employeePageable.getSize(),
               employeePageable.getTotalElements(),
               employeePageable.getTotalPages(),
               employeePageable.isLast());

    }
    public EmployeeResponseDTO findEmployeeById(Long id){
        Employee employee = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee Not found with Id"+id));
        return mapToResponse(employee);
    }
}
