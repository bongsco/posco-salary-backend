package com.bongsco.poscosalarybackend.adjust.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.Salary;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.user.domain.Employee;
import com.bongsco.poscosalarybackend.user.repository.EmployeeRepository;
import com.bongsco.poscosalarybackend.user.repository.SalaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaybandCriteriaService {
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final AdjustRepository adjustRepository;

    public MainAdjPaybandCriteriaResponse findAllPaybandCriteria(Long adjInfoId) {
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId);
        List<Employee> employees = employeeRepository.findAll();
        employees = employees.stream().filter(employee -> employee.getGrade() != null).toList();

        Map<Long, Integer> countEmpl = employees.stream() //gradeId:인원수
            .collect(Collectors.groupingBy(
                empl -> empl.getGrade().getId(), // 그룹화 기준
                Collectors.summingInt(e -> 1) // 각 그룹의 개수 세기
            ));

        Long beforeAdjInfoId = adjustRepository.findLatestAdjustInfo(adjInfoId).get(0).getId();
        //없으면 에러

        List<Salary> salaries = salaryRepository.findByAdjInfo_Id(beforeAdjInfoId);

        Map<Long, BigDecimal> representativeVal = salaries.stream() //gradeId:대표값
            .collect(Collectors.groupingBy(
                s -> s.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> calculateMedian(list) // 상위 5% 제외 후 중간값
                )
            ));

        return new MainAdjPaybandCriteriaResponse(paybandCriterias
            .stream()
            .map(pc -> {
                Integer count = countEmpl.getOrDefault(pc.getGrade().getId(), 0); // null이면 0 반환
                BigDecimal representative = representativeVal.getOrDefault(pc.getGrade().getId(),
                    BigDecimal.valueOf(0)); // null이면 0 반환
                return MainAdjPaybandCriteriaResponse.PaybandCriteriaResponse.from(pc, count, representative);
            })
            .toList());
    }

    private BigDecimal calculateMedian(List<Salary> salaries) {
        List<Salary> salaryPerGrade = salaries.stream()
            .sorted(Comparator.comparing(Salary::getStdSalary))
            .toList();

        if (salaryPerGrade.isEmpty()) {
            return BigDecimal.valueOf(0);
        }

        int size = salaryPerGrade.size();
        int middle = size / 2;

        if (size % 2 == 0) {
            // 짝수 개수일 때: 중앙 두 값의 평균 반환
            return roundToThousands(salaryPerGrade.get(middle - 1)
                .getStdSalary()
                .add(salaryPerGrade.get(middle).getStdSalary()).divide(BigDecimal.valueOf(2),
                    RoundingMode.HALF_UP));
        } else {
            // 홀수 개수일 때: 가운데 값 반환
            return roundToThousands(salaryPerGrade.get(middle).getStdSalary());
        }
    }

    private BigDecimal roundToThousands(BigDecimal value) {
        BigDecimal thousand = BigDecimal.valueOf(1000);
        return value.divide(thousand, 0, RoundingMode.HALF_UP).multiply(thousand);
    }
}
