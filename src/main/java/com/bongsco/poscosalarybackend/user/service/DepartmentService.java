package com.bongsco.poscosalarybackend.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.user.dto.response.DepartmentResponse;
import com.bongsco.poscosalarybackend.user.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepart() {
        return departmentRepository.findAll().stream().map(DepartmentResponse::from).toList();
    }
}
