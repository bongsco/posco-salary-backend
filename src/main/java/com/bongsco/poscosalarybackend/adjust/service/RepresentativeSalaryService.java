package com.bongsco.poscosalarybackend.adjust.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.RepresentativeSalary;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class RepresentativeSalaryService {

    public static Double getRepresentativeVal(List<RepresentativeSalary> representativeSalaries, Long gradeId) {
        return representativeSalaries.stream()
            .filter(representativeSalary -> !representativeSalary.getDeleted())
            .filter(val -> val.getGrade().getId() == gradeId)
            .findFirst()
            .map(RepresentativeSalary::getRepresentativeVal) // 대표값 가져오기
            .orElse(0.0);
    }
}
