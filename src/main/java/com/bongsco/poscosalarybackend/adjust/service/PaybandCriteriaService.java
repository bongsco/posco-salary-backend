package com.bongsco.poscosalarybackend.adjust.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaybandCriteriaService {
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    // private final EmployeeRepository employeeRepository;
    // private final SalaryRepository salaryRepository;

    public List<MainAdjPaybandCriteriaResponse> findAllPaybandCriteria(Long adjInfoId) {
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId);
        //List<Employee> employees = employeeRepository.findAll();
        //employees.stream()
        //                 .collect(Collectors.groupingBy(
        //                         empl-> empl.getGrade().getId(), // 그룹화 기준
        //                         Collectors.summingInt(e -> 1) // 각 그룹의 개수 세기
        //                 ));

        //List<Salary> salaries=salaryRepository.findByAdjInfo_Id(adjInfoId-1);
        // Map<Long, BigDecimal> representativeVal = employees.stream()
        //     .collect(Collectors.groupingBy(
        //         e -> e.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
        //         Collectors.collectingAndThen(
        //             Collectors.toList(), // 해당 그룹의 직원 리스트
        //             list -> calculateAverageExcludingTop5Percent(list, salaries) // 상위 5% 제외 후 평균 연봉 계산
        //         )
        //     ));

        return paybandCriterias
            .stream()
            .map(pc -> MainAdjPaybandCriteriaResponse.from(pc, 0,
                BigDecimal.valueOf(0))) //map.get(pc.getGrade().getId()))
            .toList();
    }

    // private double calculateAverageExcludingTop5Percent(List<Employee> employees, List<Salary> salaries) {
    //     if (employees.isEmpty()) {
    //         return 0.0;
    //     }
    //
    //     List<Salary> salaryPerGrade = salaries.stream()
    //         .filter(s -> s.getGrade().getId().equals(employees.get(0).getGrade().getId()))
    //         .sorted()
    //         .toList();
    //
    //     int totalEmployees = salaryPerGrade.size();
    //     int excludeCount = (int)Math.ceil(totalEmployees * 0.05); // 상위 5% 제외할 개수
    //
    //     // 하위 (100% - 5%)만 선택
    //     List<Salary> filteredSalaries = salaryPerGrade.subList(0, totalEmployees - excludeCount);
    //
    //     // 평균 연봉 계산
    //     BigDecimal sum = filteredSalaries.stream()
    //         .map(Salary::getStdSalary)
    //         .reduce(BigDecimal.ZERO, BigDecimal::add);
    //
    //     return filteredSalaries.isEmpty() ? 0.0 :
    //         sum.divide(BigDecimal.valueOf(filteredSalaries.size()), 100, RoundingMode.HALF_UP).doubleValue(); //TODO : 평균에서 중위값으로 바꾸기
    // }
}
