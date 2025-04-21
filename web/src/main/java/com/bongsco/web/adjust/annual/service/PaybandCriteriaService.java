package com.bongsco.web.adjust.annual.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bongsco.web.adjust.annual.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.web.adjust.annual.entity.PaybandCriteria;
import com.bongsco.web.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.web.adjust.common.entity.RepresentativeSalary;
import com.bongsco.web.adjust.common.repository.RepresentativeSalaryRepository;
import com.bongsco.web.employee.entity.Employee;
import com.bongsco.web.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaybandCriteriaService {
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final RepresentativeSalaryRepository representativeSalaryRepository;
    private final AdjustService adjustService;


}
