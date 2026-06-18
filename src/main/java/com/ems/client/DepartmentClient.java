package com.ems.client;

import com.ems.dto.DepartmentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="department-service", url="http://localhost:8761")
public interface DepartmentClient {

    @GetMapping("/departments/{id}")
    DepartmentResponseDTO getDepartmentById(@PathVariable("id") Long id);
}
