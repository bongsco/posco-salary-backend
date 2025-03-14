package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.CompensationEmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.PreprocessAdjSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjSubjectRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankIncrementRateRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.user.domain.Employee;
import com.bongsco.poscosalarybackend.user.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjSubjectService {
    private final AdjSubjectRepository adjSubjectRepository;
    private final RankIncrementRateRepository rankIncrementRateRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse> findAll(Long adjInfoId) {
        // 연봉조정차수를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId);

        return subjects.stream().filter(adjSubject -> !adjSubject.getDeleted()).map(EmployeeResponse::from).toList();
    }

    public List<EmployeeResponse> findBySearchKey(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().filter(adjSubject -> !adjSubject.getDeleted()).map(EmployeeResponse::from).toList();
    }

    @Transactional
    public void updateSubjectUseEmployee(
        Long adjInfoId,
        ChangedSubjectUseEmployeeRequest changedSubjectUseEmployeeRequest
    ) {
        changedSubjectUseEmployeeRequest.getChangedSubjectUseEmployee()
            .forEach(changedSubjectUseEmployee -> {
                AdjSubject adjSubject = adjSubjectRepository.findByAdjInfoIdAndEmployeeId(adjInfoId,
                        changedSubjectUseEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                AdjSubject saveAdjSubject = adjSubject
                    .toBuilder()
                    .subjectUse(changedSubjectUseEmployee.getSubjectUse())
                    .build();
                adjSubjectRepository.save(saveAdjSubject);
            });
    }

    public PreprocessAdjSubjectsResponse findCompensationAll(Long adjInfoId) {
        // 연봉조정차수를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId);

        return new PreprocessAdjSubjectsResponse(subjects.stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
    }

    private CompensationEmployeeResponse convertToResponse(AdjSubject adjSubject) {
        // CompensationEmployeeResponse 객체를 구성하는 함수
        CompensationEmployeeResponse response = new CompensationEmployeeResponse(
            adjSubject.getId(),
            adjSubject.getEmployee().getEmpNum(),
            adjSubject.getEmployee().getName(),
            adjSubject.getEmployee().getDepartment().getDepName(),
            adjSubject.getGrade().getGradeName(),
            adjSubject.getRank().getRankCode(),
            adjSubject.getInHighPerformGroup(),
            adjSubject.getAdjInfo().getEvalAnnualSalaryIncrement(),
            adjSubject.getAdjInfo().getEvalPerformProvideRate()
        );

        RankIncrementRate rankIncrementRate = rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(
            adjSubject.getRank().getId(),
            adjSubject.getAdjInfo().getId(),
            adjSubject.getGrade().getId()
        ).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        if (rankIncrementRate != null) {
            response.setEvalDiffIncrement(rankIncrementRate.getEvalDiffIncrement());
            response.setEvalDiffBonus(rankIncrementRate.getEvalDiffBonus());
        }

        return response;
    }

    public PreprocessAdjSubjectsResponse findCompensationBySearchKey(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return new PreprocessAdjSubjectsResponse(subjects.stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
    }

    public void updateHighPerformGroupEmployee(
        Long adjInfoId,
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
                adjSubjectRepository.save(saveAdjSubject);
            });
    }

    public List<EmployeeResponse> findOne(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    public MainAdjPaybandBothSubjectsResponse getBothUpperLowerSubjects(Long adjInfoId) { //상한, 하한 초과자 가져오기
        return new MainAdjPaybandBothSubjectsResponse(getUpperSubjects(adjInfoId), getLowerSubjects(adjInfoId));
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getUpperSubjects(
        Long adjInfoId
    ) {     //상한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndUpper(
            adjInfoId);

        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) > 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId()).get();
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getDepName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjects(
        Long adjInfoId
    ) {     //하한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndLower(
            adjInfoId);

        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) < 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId()).get();
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getDepName())
                    .gradeName(employee.getGrade().getGradeName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public void modifyAdjustSubject(Long adjSubjectId, Boolean paybandUse) {
        AdjSubject adjSubject = adjSubjectRepository.findById(adjSubjectId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        AdjSubject saveAdjSubject = adjSubject.toBuilder()
            .paybandUse(paybandUse)
            .build();
        adjSubjectRepository.save(saveAdjSubject);
    }

    public MainAdjPaybandBothSubjectsResponse getBothUpperLowerSubjectsWithSearchKey(Long adjInfoId, String searchKey) {
        return new MainAdjPaybandBothSubjectsResponse(getUpperSubjectsWithSearchKey(adjInfoId, searchKey),
            getLowerSubjectsWithSearchKey(adjInfoId, searchKey));
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getUpperSubjectsWithSearchKey(
        Long adjInfoId, String searchKey
    ) {
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndUpperWithSearchKey(
            adjInfoId, searchKey);

        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) > 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId()).get();
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getDepName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjectsWithSearchKey(
        Long adjInfoId, String searchKey
    ) {
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndLowerWithSearchKey(
            adjInfoId, searchKey);
        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) < 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId()).get();
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getDepName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }
}
