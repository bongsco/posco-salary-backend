package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

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

        return subjects.stream().map(EmployeeResponse::from).toList();
    }

    public List<EmployeeResponse> findBySearchKey(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjSubject> subjects = adjSubjectRepository.findByAdjInfoIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
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
            adjSubject.getEmployee().getGrade().getGradeName(),
            adjSubject.getEmployee().getRank().getRankCode(),
            adjSubject.getInHighPerformGroup(),
            adjSubject.getAdjInfo().getEvalAnnualSalaryIncrement(),
            adjSubject.getAdjInfo().getEvalPerformProvideRate()
        );

        RankIncrementRate rankIncrementRate = rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(
            adjSubject.getEmployee().getRank().getId(), //rank는 도메인 바꾸고 바꾸기, 당시 랭크가 저장 되어 있어야함
            adjSubject.getAdjInfo().getId(),
            adjSubject.getEmployee().getGrade().getId() //rank는 도메인 바꾸고 바꾸기, 당시 직급이 저장 되어 있어야함 얘도,,,
        );

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

    @Transactional
    public void updateEmployeeSubjectUse(Long adjInfoId, ChangedEmployeeRequest changedEmployeeRequest) {
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
        Long adj_info_id
    ) {     //상한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndUpper(
            adj_info_id);

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
                    .rankName(employee.getRank().getRankName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjects(
        Long adj_info_id
    ) {     //하한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndLower(
            adj_info_id);

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
                    .rankName(employee.getRank().getRankName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public void modifyAdjustSubject(Long adjSubjectId, Boolean paybandUse) {
        AdjSubject saveAdjSubject = adjSubjectRepository.findById(adjSubjectId)
            .get()
            .toBuilder()
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
                    .rankName(employee.getRank().getRankName())
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
                    .rankName(employee.getRank().getRankName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }
}
