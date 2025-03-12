package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.CompensationEmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjSubjectRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankIncrementRateRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjSubjectService {
    private final AdjSubjectRepository adjSubjectRepository;
    private final RankIncrementRateRepository rankIncrementRateRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;

    public List<EmployeeResponse> findAll(long adjInfoId) {
        // 연봉조정차수를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    public List<EmployeeResponse> findBySearchKey(long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    @Transactional
    public void updateSubjectUseEmployee(
        long adjInfoId,
        ChangedSubjectUseEmployeeRequest changedSubjectUseEmployeeRequest
    ) {
        changedSubjectUseEmployeeRequest.getChangedSubjectUseEmployee()
            .forEach(changedSubjectUseEmployee -> {
                AdjSubject adjSubject = adjSubjectRepository.findByAdjInfoIdAndEmployeeId(adjInfoId,
                        changedSubjectUseEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                /* subjectUse값 세팅 및 저장 */
                //adjSubject.setSubjectUse(changedSubjectUseEmployee.getSubjectUse());
                AdjSubject saveAdjSubject = adjSubject
                    .toBuilder()
                    .subjectUse(changedSubjectUseEmployee.getSubjectUse())
                    .build();
                adjSubjectRepository.save(adjSubject);
            });
    }

    public List<CompensationEmployeeResponse> findCompensationAll(long adjInfoId) {
        // 연봉조정차수를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId);

        return subjects.stream()
            .filter(AdjSubject::getSubjectUse)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private CompensationEmployeeResponse convertToResponse(AdjSubject adjSubject) {
        // CompensationEmployeeResponse 객체를 구성하는 함수
        CompensationEmployeeResponse response = new CompensationEmployeeResponse(
            adjSubject.getId(),
            adjSubject.getEmployee().getEmpNum(),
            adjSubject.getEmployee().getName(),
            adjSubject.getEmployee().getDepartment().getDepName(),
            adjSubject.getEmployee().getGrade().getGradeName(),
            adjSubject.getEmployee().getRank().getRankCode(),
            adjSubject.getInHighPerformGroup(),
            adjSubject.getAdjInfo().getEvalAnnualSalaryIncrement(),
            adjSubject.getAdjInfo().getEvalPerformProvideRate()
        );

        RankIncrementRate rankIncrementRate = rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(
            adjSubject.getEmployee().getRank().getId(),
            adjSubject.getAdjInfo().getId(),
            adjSubject.getEmployee().getGrade().getId()
        );

        if (rankIncrementRate != null) {
            response.setEvalDiffIncrement(rankIncrementRate.getEvalDiffIncrement());
            response.setEvalDiffBonus(rankIncrementRate.getEvalDiffBonus());
        }

        return response;
    }

    public List<CompensationEmployeeResponse> findCompensationBySearchKey(long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream()
            .filter(AdjSubject::getSubjectUse)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public void updateHighPerformGroupEmployee(
        long adjInfoId,
        ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        changedHighPerformGroupEmployeeRequest.getChangedHighPerformGroupEmployee()
            .forEach(changedHighPerformGroupEmployee -> {
                AdjSubject adjSubject = adjSubjectRepository.findByAdjInfoIdAndEmployeeId(adjInfoId,
                        changedHighPerformGroupEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                /* setInHighPerformGroup값 세팅 및 저장 */
                AdjSubject saveAdjSubject = adjSubject.toBuilder()
                    .inHighPerformGroup(changedHighPerformGroupEmployee.getInHighPerformGroup())
                    .build();
                adjSubjectRepository.save(adjSubject);
            });
    }

    public List<EmployeeResponse> findOne(long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    @Transactional
    public void updateEmployeeSubjectUse(long adjInfoId, ChangedEmployeeRequest changedEmployeeRequest) {
        changedEmployeeRequest.getChangedEmployee().forEach(changedEmployee -> {
            AdjSubject adjSubject = adjSubjectRepository.findByAdjInfoIdAndEmployeeId(adjInfoId,
                    changedEmployee.getEmployeeId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
            AdjSubject saveAdjSubject = adjSubject.toBuilder().subjectUse(changedEmployee.getSubjectUse()).build();
            adjSubjectRepository.save(adjSubject);
        });
    }

    public MainAdjPaybandBothSubjectsResponse getBothUpperLowerSubjects(Long adj_info_id) { //상한, 하한 초과자 가져오기
        return new MainAdjPaybandBothSubjectsResponse(getUpperSubjects(adj_info_id), getLowerSubjects(adj_info_id));
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getUpperSubjects(
        Long adj_info_id) { //상한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalary(
            adj_info_id);

        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                BigDecimal upperLimitPrice = paybandCriteriaRepository.findByAdjInfo_IdAndGrade_Id(adj_info_id,
                    adjSubjectSalaryDto.getGradeId()).getUpperLimitPrice();
                AdjSubjectSalaryDto newAdjSubjectSalaryDto = adjSubjectSalaryDto.toBuilder()
                    .limitPrice(upperLimitPrice)
                    .build();
                return adjSubjectSalaryDto.getStdSalary().compareTo(upperLimitPrice) > 0;
            })
            .map(MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse::from)
            .toList();

        //pc에서의 직급 id가 dto와 똑같은걸찾고, 그 pc의 상한값과 비교
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjects(
        Long adj_info_id) { //하한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalary(
            adj_info_id);

        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                BigDecimal lowerLimitPrice = paybandCriteriaRepository.findByAdjInfo_IdAndGrade_Id(adj_info_id,
                    adjSubjectSalaryDto.getGradeId()).getLowerLimitPrice();
                AdjSubjectSalaryDto newAdjSubjectSalaryDto = adjSubjectSalaryDto.toBuilder()
                    .limitPrice(lowerLimitPrice)
                    .build();
                return adjSubjectSalaryDto.getStdSalary().compareTo(lowerLimitPrice) < 0;
            })
            .map(MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse::from)
            .toList();
    }

    public Boolean modifyAdjustSubject(Long adjSubjectId, Boolean paybandUse) {
        return adjSubjectRepository.updateAdjSubjectPaybandUse(adjSubjectId, paybandUse) > 0;
    }
}
