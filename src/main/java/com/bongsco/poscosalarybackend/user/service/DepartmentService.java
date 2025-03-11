package com.bongsco.poscosalarybackend.user.service;

import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.global.exception.ErrorCode;
import com.bongsco.poscosalarybackend.user.dto.response.DepartmentResponse;
import com.bongsco.poscosalarybackend.user.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepart() {
        if(departmentRepository.findAll().isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return departmentRepository.findAll().stream().map(DepartmentResponse::from).toList();
    }

}
