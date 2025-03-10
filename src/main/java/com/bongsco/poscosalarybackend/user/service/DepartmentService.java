package com.bongsco.poscosalarybackend.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.user.dto.DepartmentDto;
import com.bongsco.poscosalarybackend.user.repository.DepartmentRepository;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentDto> getAllDepart() {
        return departmentRepository.findAll().stream().map(DepartmentDto::from).toList();
    }
}
