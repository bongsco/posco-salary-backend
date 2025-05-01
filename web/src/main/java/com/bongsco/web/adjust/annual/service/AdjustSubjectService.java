package com.bongsco.web.adjust.annual.service;

import static com.bongsco.web.common.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.web.adjust.annual.dto.AdjustSubjectSalaryCalculateDto;
import com.bongsco.web.adjust.annual.dto.HpoPerDepartmentDto;
import com.bongsco.web.adjust.annual.dto.SalaryPerGradeDto;
import com.bongsco.web.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.web.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest.ChangedHighPerformGroupEmployee;
import com.bongsco.web.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.web.adjust.annual.dto.request.PaybandApplyUpdateRequest;
import com.bongsco.web.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.web.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.web.adjust.annual.dto.response.HpoEmployeesResponse;
import com.bongsco.web.adjust.annual.dto.response.HpoSalaryInfo;
import com.bongsco.web.adjust.annual.dto.response.MainResultResponses;
import com.bongsco.web.adjust.annual.dto.response.PaybandSubjectResponse;
import com.bongsco.web.adjust.annual.dto.response.RateInfo;
import com.bongsco.web.adjust.annual.dto.response.ResultChartResponse;
import com.bongsco.web.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.web.adjust.annual.repository.reflection.MainResultProjection;
import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.adjust.common.entity.AdjustSubject;
import com.bongsco.web.adjust.common.entity.PaybandAppliedType;
import com.bongsco.web.adjust.common.repository.AdjustRepository;
import com.bongsco.web.adjust.common.repository.AdjustSubjectRepository;
import com.bongsco.web.adjust.common.repository.reflection.EmployeeAndSalaryProjection;
import com.bongsco.web.adjust.common.service.AdjustStepService;
import com.bongsco.web.common.exception.CustomException;
import com.bongsco.web.employee.entity.Employee;
import com.bongsco.web.employee.repository.EmployeeRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjustSubjectService {
    private final AdjustSubjectRepository adjustSubjectRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final AdjustRepository adjustRepository;
    private final AdjustStepService adjustStepService;
    private final EntityManager entityManager;

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

    @Transactional
    public void updateHighPerformGroupEmployee(
        Long adjustId,
        ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        // 요청 직원 ID 목록 추출
        List<Long> employeeIds = changedHighPerformGroupEmployeeRequest.getChangedHighPerformGroupEmployee().stream()
            .map(ChangedHighPerformGroupEmployee::getEmployeeId)
            .toList();

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
            .toList();

        // 모두 저장
        adjustSubjectRepository.saveAll(updatedSubjects);
        this.initializeIsPaybandApplied(adjustId);
        adjustStepService.resetMain(adjustId);
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
                if (request.getIsPaybandApplied() == PaybandAppliedType.UPPER && stdSalary > upperLimit) {
                    finalStdSalary = upperLimit;
                } else if (request.getIsPaybandApplied() == PaybandAppliedType.LOWER && stdSalary < lowerLimit) {
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
    public void initializeIsPaybandApplied(Long adjustId) {
        adjustSubjectRepository.updatePaybandAppliedTypeByAdjustId(adjustId);
        entityManager.clear();
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

            Double beforeSalary = adjustSubjectDto.getStdSalary();

            if (beforeAdjustSubject != null) {
                beforeSalary = beforeAdjustSubject.getFinalStdSalary();
            }

            //직무 기본 연봉 불러옴
            Double gradeBaseSalary = adjustSubjectDto.getBaseSalary();

            //연봉 인상률 불러옴
            Double evalDiffIncrement = adjustSubjectDto.getSalaryIncrementRate() / 100 + 1;

            //고성과 있으면
            //평차등연봉인상률 = (직급, 평가별 평차연) * (1 + 고성과평차연)
            if (subject.getIsInHpo() != null && subject.getIsInHpo()) {
                evalDiffIncrement = evalDiffIncrement * (1 + hpoSalaryIncrementByRank);
            }
            evalDiffIncrement  = evalDiffIncrement - 1;

            Double newStdSalary =
                beforeSalary
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
            if (subject.getIsInHpo() != null && subject.getIsInHpo()) {
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

    @Transactional
    public MainResultResponses getFinalResult(Long adjustId, List<String> filterEmpNum, List<String> filterName,
        List<String> filterGrade, List<String> filterDepartment, List<String> filterRank,
        List<Map<String, String>> sorts, Integer pageNumber, Integer pageSize) {

        Map<String, String> mapping = Map.of(
            "empNum", "empNum",
            "name", "name",
            "departmentName", "depName",
            "gradeName", "gradeName",
            "rankCode", "rankCode",
            "stdSalary", "finalStdSalary",
            "totalSalary", "totalSalary"
        );

        List<Sort.Order> sortOrders = new ArrayList<>(); //초기값은 id
        if (sorts != null && !sorts.isEmpty()) {
            for (Map<String, String> sort : sorts) {
                for (Map.Entry<String, String> entry : sort.entrySet()) {
                    String field = mapping.get(entry.getKey());
                    String direction = entry.getValue();

                    if ("내림차순".equals(direction)) {
                        sortOrders.add(Sort.Order.desc(field));
                    } else {
                        sortOrders.add(Sort.Order.asc(field));
                    }
                }
            }
        }

        /* 모두 다 같으면 id를 기준으로 정렬되도록 id 조건 마지막에 추가 */
        sortOrders.add(Sort.Order.desc("adjustSubjectId"));

        /* Pageable 객체 생성 */
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrders));

        Page<MainResultProjection> resultAndPageInfo = adjustSubjectRepository.findResultDtoWithPagination(
            adjustId,
            (filterEmpNum == null || filterEmpNum.isEmpty()) ? null :
                filterEmpNum.stream().map(empNum -> "%" + empNum + "%").toArray(String[]::new),
            (filterName == null || filterName.isEmpty()) ? null :
                filterName.stream().map(name -> "%" + name + "%").toArray(String[]::new),
            (filterGrade == null || filterGrade.isEmpty()) ? null :
                filterGrade.toArray(new String[0]),
            (filterDepartment == null || filterDepartment.isEmpty()) ? null :
                filterDepartment.stream().map(dep -> "%" + dep + "%").toArray(String[]::new),
            (filterRank == null || filterRank.isEmpty()) ? null :
                filterRank.toArray(new String[0]),
            pageable
        );

        Adjust adjust = adjustRepository.findById(adjustId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        List<MainResultResponses.MainResultResponse> responseList = resultAndPageInfo.getContent().stream().map(dto -> {
            String paybandResult = Optional.ofNullable(dto.getIsPaybandApplied())
                .map(t -> switch (t) {
                    case UPPER -> "적용(상한)";
                    case LOWER -> "적용(하한)";
                    default -> "미적용";
                })
                .orElse("미적용");
            Double bonusMultiplier = Optional.ofNullable(dto.getBonusMultiplier()).orElse(0.0);


            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                dto.getEmpId());
            Double beforeSalary = dto.getBeforeStdSalary();
            Double beforeHpoBonus = dto.getBeforeHpoBonus();
            if (beforeAdjustSubject != null) {
                beforeSalary = beforeAdjustSubject.getFinalStdSalary();
                beforeHpoBonus = beforeAdjustSubject.getHpoBonus();
            }
            Double finalStdSalaryIncrementRate=((Optional.ofNullable(dto.getFinalStdSalary()).orElse(0.0) - beforeSalary) / beforeSalary) * 100;

            Double salaryIncrementRate = Optional.ofNullable(dto.getSalaryIncrementRate()).orElse(0.0);
            if (dto.getIsInHpo() != null && dto.getIsInHpo()) {
                bonusMultiplier += Optional.ofNullable(adjust.getHpoBonusMultiplier()).orElse(0.0);
                Double hpoSalaryIncrementRate = Optional.ofNullable(adjust.getHpoSalaryIncrementByRank()).orElse(0.0);
                salaryIncrementRate = ((1 + salaryIncrementRate / 100) * (1 + hpoSalaryIncrementRate / 100) - 1) * 100;
            }

            return MainResultResponses.MainResultResponse.builder()
                .empNum(dto.getEmpNum())
                .name(dto.getName())
                .gradeName(dto.getGradeName())
                .positionName(dto.getPositionName())
                .depName(dto.getDepName())
                .rankCode(dto.getRankCode())
                .salaryIncrementRate(salaryIncrementRate)
                .bonusMultiplier(bonusMultiplier)
                .stdSalaryIncrementRate(finalStdSalaryIncrementRate)
                .payband(paybandResult)
                .salaryBefore(beforeSalary)
                .stdSalary(dto.getFinalStdSalary())
                .totalSalaryBefore(beforeSalary+beforeHpoBonus)
                .totalSalary(
                    Optional.ofNullable(dto.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(dto.getHpoBonus())
                        .orElse(0.0))
                .build();
        }).toList();

        return new MainResultResponses(responseList, resultAndPageInfo.getTotalPages(),
            resultAndPageInfo.getNumber() + 1);
    }

    @Transactional
    public void changeIncrementRateAndSalaryInfo(Long adjustId) {
        List<EmployeeAndSalaryProjection> projections = adjustSubjectRepository.findAdjustSubjectIncrementDtoByAdjustId(
            adjustId);

        List<Employee> updatedEmployees = projections.stream().map(projection -> {
            Double finalStdSalary = Optional.ofNullable(projection.getFinalStdSalary()).orElse(0.0);
            Double beforeFinalStdSalary = projection.getEmployee().getStdSalary();
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                projection.getEmployee().getId());
            if (beforeAdjustSubject != null){
                beforeFinalStdSalary = beforeAdjustSubject.getFinalStdSalary();
            }
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;
            return projection.getEmployee().toBuilder().stdSalaryIncrementRate(finalStdSalaryIncrementRate).build();
        }).filter(Objects::nonNull).toList();
        employeeRepository.saveAll(updatedEmployees);
        adjustRepository.findById(adjustId).ifPresent(adjust -> {
            Adjust updatedAdjust = adjust.toBuilder().isSubmitted(true).build();
            adjustRepository.save(updatedAdjust);
        });
    }

    public ResultChartResponse getChartData(Long adjustId) {
        Adjust currentAdjust = adjustRepository.findById(adjustId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        Adjust beforeAdjust = adjustRepository.findById(adjustId-1).orElse(null);

        List<SalaryPerGradeDto> currentDto = adjustSubjectRepository.findSalaryPerDto(adjustId);
        String adjustName =currentAdjust.getYear().toString()+"년 "+currentAdjust.getOrderNumber().toString()+"차";
        List<SalaryPerGradeDto> berforeDto = new ArrayList<>();
        String beforeAdjustName= "이전 차수가 존재하지 않습니다.";
        if (beforeAdjust != null) {
            berforeDto = adjustSubjectRepository.findSalaryPerDto(beforeAdjust.getId());
            beforeAdjustName = beforeAdjust.getYear().toString()+"년 "+beforeAdjust.getOrderNumber().toString()+"차";
        }

        List<ResultChartResponse.SalaryPerGrade> salaryPerGrades = new ArrayList<>();

        Map<String, Double> beforeSalaryPerGrade = new HashMap<>();
        berforeDto.stream().forEach(dto -> {
            Double stdSalary = dto.getTotalStdSalary() != null ? dto.getTotalStdSalary() : 0.0;
            Double hpoBonus = dto.getTotalHpoBonus() != null ? dto.getTotalHpoBonus() : 0.0;
            beforeSalaryPerGrade.put(dto.getGradeName(), stdSalary + hpoBonus);
        });
        salaryPerGrades.add(ResultChartResponse.SalaryPerGrade.from(beforeAdjustName, beforeSalaryPerGrade));

        Map<String, Double> currentSalaryPerGrade = new HashMap<>();
        currentDto.stream().forEach(dto -> {
            Double stdSalary = dto.getTotalStdSalary() != null ? dto.getTotalStdSalary() : 0.0;
            Double hpoBonus = dto.getTotalHpoBonus() != null ? dto.getTotalHpoBonus() : 0.0;
            currentSalaryPerGrade.put(dto.getGradeName(), stdSalary + hpoBonus);
        });
        salaryPerGrades.add(ResultChartResponse.SalaryPerGrade.from(adjustName, currentSalaryPerGrade));


        List<ResultChartResponse.AnnualSalary> annualSalaries = new ArrayList<>();

        Double beforeSumStdSalary = berforeDto.stream()
            .map(SalaryPerGradeDto::getTotalStdSalary)     // totalStdSalary 꺼냄
            .filter(Objects::nonNull)                       // null 제외
            .mapToDouble(Double::doubleValue)
            .sum();
        Double beforeSumHpoBonus = berforeDto.stream()
            .map(SalaryPerGradeDto::getTotalHpoBonus)     // totalStdSalary 꺼냄
            .filter(Objects::nonNull)                       // null 제외
            .mapToDouble(Double::doubleValue)
            .sum();
        ResultChartResponse.AnnualSalary beforeAnnualSalary = ResultChartResponse.AnnualSalary.from(beforeAdjustName, beforeSumStdSalary, beforeSumHpoBonus, beforeSumStdSalary+beforeSumHpoBonus);
        annualSalaries.add(beforeAnnualSalary);

        Double sumStdSalary = currentDto.stream()
            .map(SalaryPerGradeDto::getTotalStdSalary)     // totalStdSalary 꺼냄
            .filter(Objects::nonNull)                       // null 제외
            .mapToDouble(Double::doubleValue)
            .sum();
        Double sumHpoBonus = currentDto.stream()
            .map(SalaryPerGradeDto::getTotalHpoBonus)     // totalStdSalary 꺼냄
            .filter(Objects::nonNull)                       // null 제외
            .mapToDouble(Double::doubleValue)
            .sum();
        ResultChartResponse.AnnualSalary currentAnnualSalary = ResultChartResponse.AnnualSalary.from(adjustName,  sumStdSalary,  sumHpoBonus, sumStdSalary+sumHpoBonus);
        annualSalaries.add(currentAnnualSalary);

        List<HpoPerDepartmentDto> HpoPerDepartment = adjustSubjectRepository.findHpoPerDepartmentDto(adjustId);

        return ResultChartResponse.from(salaryPerGrades, annualSalaries, HpoPerDepartment);
    }
}
