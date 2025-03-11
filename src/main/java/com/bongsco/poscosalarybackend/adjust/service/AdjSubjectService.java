package com.bongsco.poscosalarybackend.adjust.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjSubjectRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjSubjectService {
    private final AdjSubjectRepository adjSubjectRepository;

    public List<EmployeeResponse> findAll(long adjInfoId) {
        // 연봉조정차수를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    public List<EmployeeResponse> findOne(long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    public void updateEmployeeUse(ChangedEmployeeRequest changedEmployeeRequest) {

    }
}
