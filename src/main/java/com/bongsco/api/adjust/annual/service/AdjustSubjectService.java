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

import com.bongsco.api.adjust.annual.dto.AdjustSubjectSalaryCalculateDto;
import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest.ChangedHighPerformGroupEmployee;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.PaybandApplyUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjResultResponse;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployeesResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoSalaryInfo;
import com.bongsco.api.adjust.annual.dto.response.PaybandSubjectResponse;
import com.bongsco.api.adjust.annual.dto.response.RateInfo;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustSubject;
import com.bongsco.api.adjust.common.entity.PaybandAppliedType;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
import com.bongsco.api.adjust.common.repository.AdjustGradeRepository;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.AdjustSubjectRepository;
import com.bongsco.api.adjust.common.repository.RepresentativeSalaryRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.employee.entity.Employee;
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
    private final AdjustGradeRepository adjustGradeRepository;

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

    public List<EmployeeResponse> findAll(Long adjustId) {
        return adjustSubjectRepository.findAllEmployeeResponsesByAdjustInfoId(adjustId);
    }

    @Transactional
    public void updateSubjectUseEmployee(
        Long adjustId,
        ChangedSubjectUseEmployeeRequest changedSubjectUseEmployeeRequest
    ) {
        changedSubjectUseEmployeeRequest.getChangedSubjectUseEmployee()
            .forEach(changedSubjectUseEmployee -> {
                AdjustSubject adjustSubject = adjustSubjectRepository.findByAdjustIdAndEmployeeId(adjustId,
                        changedSubjectUseEmployee.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                AdjustSubject saveAdjustSubject = adjustSubject
                    .toBuilder()
                    .isSubject(changedSubjectUseEmployee.getSubjectUse())
                    .build();
                adjustSubjectRepository.save(saveAdjustSubject);
            });
    }

    // 연봉조정차수를 이용해 고성과조직 가산 대상 테이블 가져오기
    public HpoEmployeesResponse findHpoEmployees(Long adjustId) {
        /* HPO 정보 가져오기 */
        HpoSalaryInfo hpoInfo = adjustRepository.findHpoSalaryInfoById(
            adjustId);
        /* rank 정보 가져오기 */
        List<RateInfo> rateInfo = adjustRepository.findRateInfoByAdjustId(adjustId);
        /* HpoEmployee 찾기 */
        List<HpoEmployee> employeeResponses = adjustSubjectRepository.findByAdjustIdAndIsSubjectTrue(adjustId);

        return HpoEmployeesResponse.builder()
            .salaryIncrementByRank(rateInfo)
            .hpoSalaryInfo(hpoInfo)
            .highPerformanceEmployees(employeeResponses)
            .build();
    }

    public void updateHighPerformGroupEmployee(
        Long adjustId,
        ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        // 요청 직원 ID 목록 추출
        List<Long> employeeIds = changedHighPerformGroupEmployeeRequest.getChangedHighPerformGroupEmployee().stream()
            .map(ChangedHighPerformGroupEmployee::getEmployeeId)
            .collect(Collectors.toList());

        // 관련 AdjustSubject 엔티티들 모두 조회
        List<AdjustSubject> existingSubjects = adjustSubjectRepository.findAllByAdjustIdAndEmployeeIdIn(adjustId,
            employeeIds);

        // 조회된 엔티티들을 employeeId를 키로 하는 Map 변환
        Map<Long, AdjustSubject> subjectsMap = existingSubjects.stream()
            .collect(Collectors.toMap(subject -> subject.getEmployee().getId(), subject -> subject));

        // 업데이트할 엔티티들을 담는 리스트
        List<AdjustSubject> updatedSubjects = changedHighPerformGroupEmployeeRequest.getChangedHighPerformGroupEmployee()
            .stream()
            .map(requestItem -> {
                AdjustSubject original = subjectsMap.get(requestItem.getEmployeeId());

                if (original == null) {
                    throw new CustomException(USER_NOT_FOUND);
                }

                return original.toBuilder()
                    .isInHpo(requestItem.getIsInHpo())
                    .build();
            })
            .collect(Collectors.toList());

        // 모두 저장
        adjustSubjectRepository.saveAll(updatedSubjects);
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

    @Transactional
    public void calculateSalaryAndBonus(Long adjustId) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Double hpoSalaryIncrementByRank =
            Optional.ofNullable(adjust.getHpoSalaryIncrementByRank()).orElse(0.0) / 100;
        //5%일때 5로 들어간다는 전제하에 이렇게 해놓음

        Double hpoBonusMultiplier = Optional.ofNullable(adjust.getHpoBonusMultiplier()).orElse(0.0);

        List<Object[]> asjAndDtos = adjustSubjectRepository.findDtoByAdjustId(
            adjustId);
        List<AdjustSubject> updatedSubjects = asjAndDtos.stream().map(asjAndDto -> {

            AdjustSubject subject = (AdjustSubject)asjAndDto[0];
            AdjustSubjectSalaryCalculateDto adjustSubjectDto = (AdjustSubjectSalaryCalculateDto)asjAndDto[1];

            //기준연봉= 전년도 기준연봉+(직무기본연봉*평차등연봉인상률)

            //전년도 기준연봉 불러옴
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                adjustSubjectDto.getEmpId());

            if (beforeAdjustSubject == null)
                return null; //신입 제외

            //직무 기본 연봉 불러옴
            Double gradeBaseSalary = adjustSubjectDto.getBaseSalary();

            //연봉 인상률 불러옴
            Double evalDiffIncrement = adjustSubjectDto.getSalaryIncrementRate() / 100 + 1;

            //고성과 있으면
            //평차등연봉인상률 = (직급, 평가별 평차연) * (1 + 고성과평차연)
            if (subject.getIsInHpo()) {
                evalDiffIncrement = evalDiffIncrement * (1 + hpoSalaryIncrementByRank) - 1;
            }

            Double newStdSalary =
                (beforeAdjustSubject.getFinalStdSalary() == null ? 1000000 : beforeAdjustSubject.getFinalStdSalary())
                    + gradeBaseSalary * evalDiffIncrement;

            newStdSalary = Math.floor(newStdSalary / 1000) * 1000;

            // 평가차등 경영 성과금 = 평차금기준임금(업적연봉기본지금) * 평가차등경영성과금지급률
            // 평차금기준임금 = ((기준연봉월할액 + 직무기본연봉) / 2 ) + 12만원

            Double rankBaseStandardSalary = (newStdSalary + gradeBaseSalary) / 12 + 12;

            //평가차등경영성과금지급률 가져옴
            Double bonusMultiplier =
                adjustSubjectDto.getBonusMultiplier() / 100;

            //고성과 조직일 경우
            //평가차등경영성과금 지급률 = 평가차등경영성과금 지급률+고성과 경영성과금지급률
            if (subject.getIsInHpo()) {
                bonusMultiplier += hpoBonusMultiplier / 100;
            }

            Double newHpoBonus = (double)Math.round(rankBaseStandardSalary * bonusMultiplier);

            newHpoBonus = Math.floor(newHpoBonus / 1000) * 1000;

            return subject.toBuilder()
                .stdSalary(newStdSalary)
                .finalStdSalary(newStdSalary)
                .hpoBonus(newHpoBonus)
                .build();
        }).filter(Objects::nonNull).toList();

        adjustSubjectRepository.saveAll(updatedSubjects);
    }

    public AdjResultResponse getFinalResult(Long adjInfoId) {
        List<AdjustSubject> adjustSubjects = adjustSubjectRepository.findByAdjust_Id(adjInfoId)
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

    public void changeIncrementRate(Long adjustId) {
        List<Object[]> employeeAndDtos = adjustSubjectRepository.findAdjustSubjectIncrementDtoByAdjustId(
            adjustId);

        List<Employee> updatedEmployees = employeeAndDtos.stream().map(employeeAndDto -> {
            Employee employee = (Employee)employeeAndDto[0];
            Double finalStdSalary = (Double)employeeAndDto[1];

            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                employee.getId());
            if (beforeAdjustSubject == null)
                return null; //신입 제외

            Double beforeFinalStdSalary = beforeAdjustSubject.getFinalStdSalary();
            if (beforeFinalStdSalary == null || finalStdSalary == null) {
                return null;
            }
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;
            return employee.toBuilder().stdSalaryIncrementRate(finalStdSalaryIncrementRate).build();
        }).filter(Objects::nonNull).toList();
        employeeRepository.saveAll(updatedEmployees);
    }
}
