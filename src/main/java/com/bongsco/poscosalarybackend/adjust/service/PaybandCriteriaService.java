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

    public List<MainAdjPaybandCriteriaResponse> findAllPaybandCriteria(Long adjInfoId) {
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId);
        List<Employee> employees = employeeRepository.findAll();
        employees = employees.stream().filter(employee -> employee.getGrade() != null).toList();

        Map<Long, Integer> countEmpl = employees.stream() //gradeId:인원수
            .collect(Collectors.groupingBy(
                empl -> empl.getGrade().getId(), // 그룹화 기준
                Collectors.summingInt(e -> 1) // 각 그룹의 개수 세기
            ));

        Long beforeAdjInfoId = adjInfoId; //TODO: 현아 언니 끝나면 직전 정기연봉 id 가져오는 코드 작성
        List<Salary> salaries = salaryRepository.findByAdjInfo_Id(beforeAdjInfoId);

        Map<Long, BigDecimal> representativeVal = employees.stream() //gradeId:대표값
            .collect(Collectors.groupingBy(
                e -> e.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
                Collectors.collectingAndThen(
                    Collectors.toList(), // 해당 그룹의 직원 리스트
                    list -> calculateAverageExcludingTop5Percent(list, salaries) // 상위 5% 제외 후 중간값
                )
            ));

        return paybandCriterias
            .stream()
            .map(pc -> {
                Integer count = countEmpl.getOrDefault(pc.getGrade().getId(), 0); // null이면 0 반환
                BigDecimal representative = representativeVal.getOrDefault(pc.getGrade().getId(),
                    BigDecimal.valueOf(0)); // null이면 0 반환
                return MainAdjPaybandCriteriaResponse.from(pc, count, representative);
            })
            .toList();
    }

    private BigDecimal calculateAverageExcludingTop5Percent(List<Employee> employees, List<Salary> salaries) {
        if (employees.isEmpty()) {
            return BigDecimal.valueOf(0);
        }

        List<Salary> salaryPerGrade = salaries.stream()
            .filter(s -> s.getGrade().getId().equals(employees.get(0).getGrade().getId()))
            .sorted(Comparator.comparing(Salary::getStdSalary))
            .toList();

        int totalEmployees = salaryPerGrade.size();
        int excludeCount = (int)Math.ceil(totalEmployees * 0.05); // 상위 5% 제외할 개수

        // 하위 (100% - 5%)만 선택
        List<Salary> filteredSalaries = salaryPerGrade.subList(0, totalEmployees - excludeCount);

        if (filteredSalaries.isEmpty()) {
            return BigDecimal.valueOf(0);
        }

        int size = filteredSalaries.size();
        int middle = size / 2;

        if (size % 2 == 0) {
            // 짝수 개수일 때: 중앙 두 값의 평균 반환
            return roundToThousands(filteredSalaries.get(middle - 1)
                .getStdSalary()
                .add(filteredSalaries.get(middle).getStdSalary())).divide(BigDecimal.valueOf(2),
                RoundingMode.HALF_UP);
        } else {
            // 홀수 개수일 때: 가운데 값 반환
            return roundToThousands(filteredSalaries.get(middle).getStdSalary());
        }
    }

    private BigDecimal roundToThousands(BigDecimal value) {
        BigDecimal hundred = BigDecimal.valueOf(1000);
        return value.divide(hundred, 0, RoundingMode.HALF_UP).multiply(hundred);
    }
}
