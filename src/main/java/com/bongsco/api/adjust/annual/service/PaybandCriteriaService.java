package com.bongsco.api.adjust.annual.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bongsco.api.adjust.annual.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
import com.bongsco.api.adjust.common.repository.PaybandCriteriaRepository;
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

    public MainAdjPaybandCriteriaResponse findAllPaybandCriteria(Long adjInfoId) {
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjustId(adjInfoId);
        List<Employee> employees = employeeRepository.findAll();
        employees = employees.stream()
            .filter(employee -> !employee.getDeleted())
            .filter(employee -> employee.getGrade() != null)
            .toList();

        Map<Long, Integer> countEmpl = employees.stream() //gradeId:인원수
            .collect(Collectors.groupingBy(
                empl -> empl.getGrade().getId(), // 그룹화 기준
                Collectors.summingInt(e -> 1) // 각 그룹의 개수 세기
            ));

        Long beforeAdjInfoId = adjustService.getBeforeAdjInfoId(adjInfoId);

        List<RepresentativeSalary> representativeSalaries = representativeSalaryRepository.findByAdjustId(
            beforeAdjInfoId);

        return new MainAdjPaybandCriteriaResponse(paybandCriterias
            .stream()
            .filter(pc -> !pc.getDeleted())
            .map(pc -> {
                Integer count = countEmpl.getOrDefault(pc.getGrade().getId(), 0); // null이면 0 반환
                Double representative = RepresentativeSalaryService.getRepresentativeVal(representativeSalaries,
                    pc.getGrade().getId());

                return MainAdjPaybandCriteriaResponse.PaybandCriteriaResponse.from(pc, count, representative);
            })
            .toList());
    }
}
