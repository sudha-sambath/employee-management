package com.ems.service;

import com.ems.dto.EmployeeFilter;
import com.ems.dto.EmployeeSearchDTO;
import com.ems.entity.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class EmployeeSpecificationService {

    public static Specification<Employee> searchByCriteria(EmployeeSearchDTO searchDTO){
        List<Predicate> predicates = new ArrayList<>();
        return (root, query, cb) -> {

            if(searchDTO.getName() != null){
                predicates.add(cb.like(cb.lower(root.get("name")),"%"+ searchDTO.getName().toLowerCase() +"%"));
            }

            if(searchDTO.getDepartment() != null){
                predicates.add(cb.equal(root.get("department"),searchDTO.getDepartment()));
            }

            if(searchDTO.getMinSalary() != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("salary"),searchDTO.getMinSalary()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Employee> filter(EmployeeFilter filter){
        return (root,query,cb)->{
            Predicate predicate = cb.conjunction();
            if(filter.getName() != null){
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }
            if(filter.getDepartment() != null){
                predicate = cb.and(predicate,
                        cb.equal(root.get("department"), filter.getDepartment()));
            }

            if(filter.getMinSalary() != null){
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("salary"), filter.getMinSalary()));
            }

            if(filter.getMaxSalary() != null){
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("salary"), filter.getMaxSalary()));
            }
            return predicate;
        };
    }
}
