package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjSubjectRepository;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.user.domain.Employee;
import com.bongsco.poscosalarybackend.user.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaybandCriteriaService {
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final AdjustRepository adjustRepository;
    private final AdjSubjectRepository adjSubjectRepository;

    public MainAdjPaybandCriteriaResponse findAllPaybandCriteria(Long adjInfoId) {
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId);
        List<Employee> employees = employeeRepository.findAll();
        employees = employees.stream().filter(employee -> employee.getGrade() != null).toList();

        Map<Long, Integer> countEmpl = employees.stream() //gradeId:인원수
            .collect(Collectors.groupingBy(
                empl -> empl.getGrade().getId(), // 그룹화 기준
                Collectors.summingInt(e -> 1) // 각 그룹의 개수 세기
            ));

        List<AdjInfo> adjInfos = adjustRepository.findLatestAdjustInfo(adjInfoId);
        if (adjInfos.isEmpty()) {
            throw new CustomException(CANNOT_NULL_INPUT);
        }
        Long beforeAdjInfoId = adjInfos.get(0).getId();

        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(beforeAdjInfoId);

        Map<Long, Double> representativeVal = adjSubjects.stream()
            .filter(adjSubject -> adjSubject.getStdSalary() != null)//gradeId:대표값
            .collect(Collectors.groupingBy(
                s -> s.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> calculateMedian(list) // 상위 5% 제외 후 중간값
                )
            ));

        return new MainAdjPaybandCriteriaResponse(paybandCriterias
            .stream()
            .filter(pc -> !pc.getDeleted())
            .map(pc -> {
                Integer count = countEmpl.getOrDefault(pc.getGrade().getId(), 0); // null이면 0 반환
                Double representative = representativeVal.getOrDefault(pc.getGrade().getId(),
                    0.0); // null이면 0 반환
                return MainAdjPaybandCriteriaResponse.PaybandCriteriaResponse.from(pc, count, representative);
            })
            .toList());
    }

    private Double calculateMedian(List<AdjSubject> adjSubjects) {
        if (adjSubjects.isEmpty()) {
            return 0.0;
        }

        List<AdjSubject> salaryPerGrade = adjSubjects.stream()
            .sorted(Comparator.comparing(AdjSubject::getStdSalary))
            .toList();

        int size = salaryPerGrade.size();
        int middle = size / 2;

        if (size % 2 == 0) {
            // 짝수 개수일 때: 중앙 두 값의 평균 반환
            return Math.round((salaryPerGrade.get(middle - 1)
                .getStdSalary()
                + salaryPerGrade.get(middle).getStdSalary()) / 2.0 / 1000.0) * 1000.0;
        } else {
            // 홀수 개수일 때: 가운데 값 반환
            return Math.round(salaryPerGrade.get(middle).getStdSalary() / 1000.0) * 1000.0;
        }
    }
}
