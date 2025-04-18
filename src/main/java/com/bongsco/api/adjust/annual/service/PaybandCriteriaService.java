package com.bongsco.api.adjust.annual.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bongsco.api.adjust.annual.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
import com.bongsco.api.adjust.common.repository.RepresentativeSalaryRepository;
import com.bongsco.api.employee.entity.Employee;
import com.bongsco.api.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaybandCriteriaService {
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final RepresentativeSalaryRepository representativeSalaryRepository;
    private final AdjustService adjustService;


}
