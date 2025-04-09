package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.PaybandApplyUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjResultResponse;
import com.bongsco.api.adjust.annual.dto.response.CompensationEmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.PaybandSubjectResponse;
import com.bongsco.api.adjust.annual.dto.response.PreprocessAdjSubjectsResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustSubject;
import com.bongsco.api.adjust.common.entity.PaybandAppliedType;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.AdjustSubjectRepository;
import com.bongsco.api.adjust.common.repository.RepresentativeSalaryRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.employee.entity.Employee;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.repository.EmployeeRepository;
import com.bongsco.api.employee.repository.GradeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjustSubjectService {
    private final AdjustSubjectRepository adjustSubjectRepository;
    private final SalaryIncrementByRankRepository salaryIncrementByRankRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final AdjustRepository adjustRepository;
    private final RepresentativeSalaryRepository representativeSalaryRepository;
    private final GradeRepository gradeRepository;
    private final AdjustService adjustService;

    public static Double calculateMedian(List<AdjustSubject> adjustSubjects) {
        if (adjustSubjects.isEmpty()) {
            return 0.0;
        }

        List<AdjustSubject> salaryPerGrade = adjustSubjects.stream()
            .sorted(Comparator.comparing(AdjustSubject::getFinalStdSalary))
            .toList();

        int size = salaryPerGrade.size();
        int middle = size / 2;

        if (size % 2 == 0) {
            // 짝수 개수일 때: 중앙 두 값의 평균 반환
            return Math.round((salaryPerGrade.get(middle - 1)
                .getFinalStdSalary()
                + salaryPerGrade.get(middle).getFinalStdSalary()) / 2.0 / 1000.0) * 1000.0;
        } else {
            // 홀수 개수일 때: 가운데 값 반환
            return Math.round(salaryPerGrade.get(middle).getFinalStdSalary() / 1000.0) * 1000.0;
        }
    }

    public List<EmployeeResponse> findAll(Long adjInfoId) {
        // 연봉조정차수를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustId(adjInfoId);

        return subjects.stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .map(EmployeeResponse::from)
            .toList();
    }

    @Transactional
    public void updateSubjectUseEmployee(
        Long adjInfoId,
        ChangedSubjectUseEmployeeRequest changedSubjectUseEmployeeRequest
    ) {
        changedSubjectUseEmployeeRequest.getChangedSubjectUseEmployee()
            .forEach(changedSubjectUseEmployee -> {
                AdjustSubject adjustSubject = adjustSubjectRepository.findByAdjustIdAndEmployeeId(adjInfoId,
                        changedSubjectUseEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                AdjustSubject saveAdjustSubject = adjustSubject
                    .toBuilder()
                    .isSubject(changedSubjectUseEmployee.getSubjectUse())
                    .build();
                adjustSubjectRepository.save(saveAdjustSubject);
            });
    }

    public PreprocessAdjSubjectsResponse findCompensationAll(Long adjInfoId) {
        // 연봉조정차수를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustId(adjInfoId);

        return new PreprocessAdjSubjectsResponse(subjects.stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
    }

    private CompensationEmployeeResponse convertToResponse(AdjustSubject adjustSubject) {
        // CompensationEmployeeResponse 객체를 구성하는 함수
        // CompensationEmployeeResponse response = new CompensationEmployeeResponse(
        //     adjustSubject.getId(),
        //     adjustSubject.getEmployee().getEmpNum(),
        //     adjustSubject.getEmployee().getName(),
        //     adjustSubject.getEmployee().getDepartment().getName(),
        //     adjustSubject.getGrade().getName(),
        //     adjustSubject.getRank().getCode(),
        //     adjustSubject.getIsInHpo(),
        //     adjustSubject.getAdjust().getHpoSalaryIncrementRateByRank(),
        //     adjustSubject.getAdjust().getHpoBonusMultiplier()
        // );
        //
        // SalaryIncrementByRank salaryIncrementByRank = salaryIncrementRateByRankRepository.findByRankIdAndAdjustIdAndGradeId(
        //     adjustSubject.getRank().getId(),
        //     adjustSubject.getAdjust().getId(),
        //     adjustSubject.getGrade().getId()
        // ).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        //
        // if (salaryIncrementByRank != null) {
        //     response.setEvalDiffIncrement(salaryIncrementByRank.getSalaryIncrementRate());
        //     response.setEvalDiffBonus(salaryIncrementByRank.getBonusMultiplier());
        // }
        //
        // return response;

        return null;
    }

    public PreprocessAdjSubjectsResponse findCompensationBySearchKey(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 고성과조직 가산 대상 테이블 가져오기
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustIdAndSearchKey(adjInfoId, searchKey);

        return new PreprocessAdjSubjectsResponse(subjects.stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .map(this::convertToResponse)
            .collect(Collectors.toList()));
    }

    public void updateHighPerformGroupEmployee(
        Long adjInfoId,
        ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        changedHighPerformGroupEmployeeRequest.getChangedHighPerformGroupEmployee()
            .forEach(changedHighPerformGroupEmployee -> {
                AdjustSubject adjustSubject = adjustSubjectRepository.findByAdjustIdAndEmployeeId(adjInfoId,
                        changedHighPerformGroupEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                /* setInHighPerformGroup값 세팅 및 저장 */
                AdjustSubject saveAdjustSubject = adjustSubject.toBuilder()
                    .isInHpo(changedHighPerformGroupEmployee.getInHighPerformGroup())
                    .build();
                adjustSubjectRepository.save(saveAdjustSubject);
            });
    }

    public PaybandSubjectResponse getBothUpperLowerSubjects(Long adjustId) {
        return new PaybandSubjectResponse(getUpperSubjects(adjustId), getLowerSubjects(adjustId));
    }

    public List<PaybandSubjectResponse.MainAdjustPaybandSubjectsResponse> getUpperSubjects(
        Long adjustId) {
        return adjustSubjectRepository.findUpperExceededSubjects(adjustId).stream()
            .map(PaybandSubjectResponse.MainAdjustPaybandSubjectsResponse::from)
            .toList();
    }

    public List<PaybandSubjectResponse.MainAdjustPaybandSubjectsResponse> getLowerSubjects(
        Long adjustId) {
        return adjustSubjectRepository.findLowerExceededSubjects(adjustId).stream()
            .map(PaybandSubjectResponse.MainAdjustPaybandSubjectsResponse::from)
            .toList();
    }

    @Transactional
    public void updateSubjectPaybandApplication(List<PaybandApplyUpdateRequest> requests) {
        List<AdjustSubject> updatedSubjects = new ArrayList<>();

        for (PaybandApplyUpdateRequest request : requests) {
            // 대상자 조회
            AdjustSubject adjustSubject = adjustSubjectRepository.findById(request.getAdjustSubjectId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            Double stdSalary = Optional.ofNullable(adjustSubject.getStdSalary()).orElse(0.0);
            Double finalStdSalary = stdSalary; // 기본값은 그대로

            Long adjustId = adjustSubject.getAdjust().getId();
            Long gradeId = adjustSubject.getGrade().getId();

            // limit 정보 조회 (상한/하한)
            PaybandCriteriaRepository.PaybandLimitInfo limitInfo =
                paybandCriteriaRepository.findLimitInfo(adjustId, gradeId).orElse(null);

            if (limitInfo != null) {
                Double upperLimit = Optional.ofNullable(limitInfo.getUpperLimit()).orElse(Double.MAX_VALUE);
                Double lowerLimit = Optional.ofNullable(limitInfo.getLowerLimit()).orElse(Double.MIN_VALUE);

                // Enum 타입에 따라 처리
                if (request.getIsPaybandApplied() == PaybandAppliedType.UPPER & stdSalary > upperLimit) {
                    finalStdSalary = upperLimit;
                } else if (request.getIsPaybandApplied() == PaybandAppliedType.LOWER & stdSalary < lowerLimit) {
                    finalStdSalary = lowerLimit;
                }
            }

            AdjustSubject updated = adjustSubject.toBuilder()
                .finalStdSalary(finalStdSalary)
                .isPaybandApplied(request.getIsPaybandApplied())
                .build();
            updatedSubjects.add(updated);
        }
        adjustSubjectRepository.saveAll(updatedSubjects);
    }

    public void calculateSalary(Long adjInfoId) {
        // List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustId(adjInfoId)
        //     .stream()
        //     .filter(adjustSubject -> !adjustSubject.getDeleted())
        //     .filter(AdjustSubject::getIsSubject)
        //     .toList();
        //
        // Adjust adjust = adjustRepository.findById(adjInfoId)
        //     .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        // Double evalAnnualSalaryIncrement =
        //     Optional.ofNullable(adjust.getHpoSalaryIncrementRateByRank()).orElse(0.0) / 100;
        // //5%일때 5로 들어간다는 전제하에 이렇게 해놓음
        //
        // adjustSubjects.stream().forEach(adjustSubject -> {
        //     //기준연봉= 전년도 기준연봉+(직무기본연봉*평차등연봉인상률)
        //
        //     //전년도 기준연봉 불러옴
        //     AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjInfoId,
        //         adjustSubject.getEmployee().getId());
        //     if (beforeAdjustSubject == null)
        //         return; //신입 제외
        //
        //     //직무 기본 연봉 불러옴
        //     Double gradeBaseSalary = adjustSubject.getGrade().getBaseSalary();
        //
        //     //연봉 인상률 불러옴
        //     SalaryIncrementByRank salaryIncrementByRank = salaryIncrementRateByRankRepository.findByRankIdAndAdjustIdAndGradeId(
        //         adjustSubject.getRank().getId(), adjInfoId, adjustSubject.getGrade().getId()).orElse(null);
        //     Double evalDiffIncrement =
        //         salaryIncrementByRank == null ? 0.0 : (salaryIncrementByRank.getSalaryIncrementRate() / 100);
        //
        //     //고성과 있으면
        //     //평차등연봉인상률 = (직급, 평가별 평차연) * (1 + 고성과평차연)
        //     if (adjustSubject.getIsInHpo()) {
        //         evalDiffIncrement = evalDiffIncrement * (1 + evalAnnualSalaryIncrement);
        //     }
        //
        //     Double newStdSalary =
        //         (beforeAdjustSubject.getFinalStdSalary() == null ? 1000000 : beforeAdjustSubject.getFinalStdSalary())
        //             + gradeBaseSalary * evalDiffIncrement;
        //
        //     AdjustSubject saveAdjustSubject = adjustSubject.toBuilder()
        //         .stdSalary(newStdSalary)
        //         .finalStdSalary(newStdSalary)
        //         .build();
        //     adjustSubjectRepository.save(saveAdjustSubject);
        // });
    }

    public void calculateAddPayment(Long adjInfoId) {
        // List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustId(adjInfoId)
        //     .stream()
        //     .filter(adjustSubject -> !adjustSubject.getDeleted())
        //     .filter(AdjustSubject::getIsSubject)
        //     .toList();
        //
        // Adjust adjust = adjustRepository.findById(adjInfoId)
        //     .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        // Double evalPerformProvideRate =
        //     Optional.ofNullable(adjust.getHpoBonusMultiplier()).orElse(100.0) / 100;
        //
        // adjustSubjects.stream().forEach(adjustSubject -> {
        //     // 평가차등 경영 성과금 = 평차금기준임금(업적연봉기본지금) * 평가차등경영성과금지급률
        //     // 평차금기준임금 = ((기준연봉월할액 + 직무기본연봉) / 2 ) + 12만원
        //
        //     // 직무기본연봉 가져옴
        //     Double gradeBaseSalary = adjustSubject.getGrade().getBaseSalary();
        //     Double rankBaseStandardSalary = (adjustSubject.getFinalStdSalary() + gradeBaseSalary) / 12 + 12;
        //
        //     //평가차등경영성과금지급률 가져옴
        //     SalaryIncrementByRank salaryIncrementByRank = salaryIncrementRateByRankRepository.findByRankIdAndAdjustIdAndGradeId(
        //         adjustSubject.getRank().getId(), adjInfoId, adjustSubject.getGrade().getId()).orElse(null);
        //     Double evalDiffBonus =
        //         salaryIncrementByRank == null ? 0.0 : (salaryIncrementByRank.getBonusMultiplier() / 100);
        //
        //     //고성과 조직일 경우
        //     //평가차등경영성과금 지급률 = 평가차등경영성과금 지급률+고성과 경영성과금지급률
        //     if (adjustSubject.getIsInHpo()) {
        //         evalDiffBonus += evalPerformProvideRate;
        //     }
        //
        //     Double newPerformAddPayment = (double)Math.round(rankBaseStandardSalary * evalDiffBonus);
        //
        //     AdjustSubject saveAdjustSubject = adjustSubject.toBuilder().hpoBonus(newPerformAddPayment).build();
        //     adjustSubjectRepository.save(saveAdjustSubject);
        // });
    }

    public AdjResultResponse getFinalResult(Long adjInfoId) {
        List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustId(adjInfoId)
            .stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .toList();

        return getAdjResultResponse(adjInfoId, adjustSubjects);
    }

    public AdjResultResponse getFinalResultWithSearchKey(Long adjInfoId, String searchKey) {
        List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustIdAndSearchKey(adjInfoId, searchKey)
            .stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .toList();

        return getAdjResultResponse(adjInfoId, adjustSubjects);
    }

    private AdjResultResponse getAdjResultResponse(Long adjInfoId, List<AdjustSubject> adjustSubjects) {
        Adjust adjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjustId(adjInfoId)
            .stream()
            .filter(pc -> !pc.getDeleted())
            .toList();

        Long beforeAdjInfoId = adjustService.getBeforeAdjInfoId(adjInfoId);

        List<RepresentativeSalary> representativeSalaries = representativeSalaryRepository.findByAdjustId(
            beforeAdjInfoId);

        return new AdjResultResponse(adjustSubjects.stream().map(adjustSubject -> {
            Employee employee = adjustSubject.getEmployee();
            //상한값 하한값 가져오기,..
            PaybandCriteria paybandCriteria = paybandCriterias.stream()
                .filter(pc -> Objects.equals(pc.getGrade().getId(), adjustSubject.getGrade().getId()))
                .findFirst()
                .orElse(null);

            //전년도 기준연봉 불러옴
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjInfoId,
                employee.getId());

            Double beforeFinalStdSalary = Optional.ofNullable(beforeAdjustSubject.getFinalStdSalary()).orElse(0.0);
            Double finalStdSalary = Optional.ofNullable(adjustSubject.getFinalStdSalary()).orElse(0.0);

            Double representativeVal = RepresentativeSalaryService.getRepresentativeVal(representativeSalaries,
                adjustSubject.getGrade().getId());
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;

            return new AdjResultResponse.AdjResult(employee.getEmpNum(), employee.getName(), employee.getBirth(),
                employee.getHireDate(), employee.getEmploymentType()
                .getName(), employee.getDepartment().getName(), employee.getPositionName(),
                adjustSubject.getGrade().getName(), employee.getPositionArea(),
                adjust.getHpoSalaryIncrementByRank(), adjust.getHpoBonusMultiplier(),
                paybandCriteria != null ? paybandCriteria.getUpperBound() : null,
                paybandCriteria != null ? paybandCriteria.getLowerBound() : null, adjustSubject.getStdSalary(),
                beforeAdjustSubject.getAdjust().getYear(), beforeAdjustSubject.getAdjust().getOrderNumber(),
                finalStdSalary,
                beforeAdjustSubject.getHpoBonus(),
                Optional.ofNullable(beforeAdjustSubject.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(
                    beforeAdjustSubject.getHpoBonus()).orElse(0.0), representativeVal,
                adjust.getYear(), adjust.getOrderNumber(), finalStdSalaryIncrementRate,
                adjustSubject.getFinalStdSalary(),
                adjustSubject.getHpoBonus(),
                Optional.ofNullable(adjustSubject.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(
                    adjustSubject.getHpoBonus()).orElse(0.0));
        }).toList());
    }

    public void calculateRepresentativeVal(Long adjInfoId) {
        List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustId(adjInfoId)
            .stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .toList();

        Adjust adjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        List<Grade> grades = gradeRepository.findAll();
        Map<Long, Double> representativeVal = adjustSubjects.stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(adjustSubject -> adjustSubject.getFinalStdSalary() != null) //gradeId:대표값
            .collect(Collectors.groupingBy(
                s -> s.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    AdjustSubjectService::calculateMedian // 상위 5% 제외 후 중간값
                )
            ));

        representativeVal.entrySet().forEach(val -> {
            representativeSalaryRepository.save(RepresentativeSalary.builder()
                .adjust(adjust)
                .grade(grades.stream()
                    .filter(grade -> Objects.equals(grade.getId(), val.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND)))
                .representativeVal(val.getValue())
                .build());
        });
    }

    public void changeIncrementRate(Long adjInfoId) {
        List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjustId(adjInfoId)
            .stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .filter(AdjustSubject::getIsSubject)
            .toList();

        adjustSubjects.forEach(adjustSubject -> {
            Employee employee = adjustSubject.getEmployee();
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjInfoId,
                employee.getId());

            Double beforeFinalStdSalary = beforeAdjustSubject.getFinalStdSalary();
            Double finalStdSalary = adjustSubject.getFinalStdSalary();
            if (beforeFinalStdSalary == null || finalStdSalary == null) {
                return;
            }
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;

            employeeRepository.save(employee.toBuilder().stdSalaryIncrementRate(finalStdSalaryIncrementRate).build());
        });
    }
}
