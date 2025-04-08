package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjResultResponse;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HighPerformanceTableResponse;
import com.bongsco.api.adjust.annual.dto.response.HighPerformanceTableResponse.HighPerformanceEmployee;
import com.bongsco.api.adjust.annual.dto.response.HighPerformanceTableResponse.HpoSalaryInfo;
import com.bongsco.api.adjust.annual.dto.response.HighPerformanceTableResponse.RateInfo;
import com.bongsco.api.adjust.annual.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustGrade;
import com.bongsco.api.adjust.common.entity.AdjustSubject;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
import com.bongsco.api.adjust.common.repository.AdjustGradeRepository;
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

    // 연봉조정차수를 이용해 고성과조직 가산 대상 테이블 가져오기
    public HighPerformanceTableResponse findHighPerformanceTable(Long adjustId) {
        /* HPO 정보 가져오기 */
        Adjust adjust = adjustRepository.findById(adjustId).get();

        HpoSalaryInfo hpoInfo = HpoSalaryInfo.builder()
            .hpoSalaryIncrementRate(adjust.getHpoSalaryIncrementByRank()) // 실제 필드/메소드명 확인!
            .hpoBonusMultiplier(adjust.getHpoBonusMultiplier())       // 실제 필드/메소드명 확인!
            .build();

        /* SalaryIncrementByRank 정보 가져오기 */
        List<SalaryIncrementByRank> allRankRates = salaryIncrementByRankRepository.findByAdjustGradeIdIn(
            adjustGradeRepository.findByAdjustId(adjustId).stream().map(AdjustGrade::getId).collect(Collectors.toList())
        );

        Map<String, Map<String, RateInfo>> salaryStructureMap = new HashMap<>();
        for (SalaryIncrementByRank rankRate : allRankRates) {
            String rankName = rankRate.getRank().getCode();
            String gradeName = rankRate.getAdjustGrade().getGrade().getName();

            RateInfo rateInfo = RateInfo.builder()
                .salaryIncrementRate(rankRate.getSalaryIncrementRate())
                .bonusMultiplier(rankRate.getBonusMultiplier())
                .build();

            Map<String, RateInfo> innerMap = salaryStructureMap.computeIfAbsent(gradeName, k -> new HashMap<>());
            innerMap.put(rankName, rateInfo);
        }

        /* Table에 들어갈 HighPerformanceEmployees 찾기 */
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustIdAndIsSubjectTrue(adjustId);
        List<HighPerformanceEmployee> employeeResponses = subjects.stream()
            .filter(adjustSubject -> !adjustSubject.getDeleted())
            .map(this::convertToResponse)
            .collect(Collectors.toList());

        return HighPerformanceTableResponse.builder()
            .salaryIncrementByRank(salaryStructureMap)
            .hpoSalaryInfo(hpoInfo)
            .highPerformanceEmployees(employeeResponses)
            .build();
    }

    private HighPerformanceEmployee convertToResponse(AdjustSubject adjustSubject) {
        // HighPerformanceTableResponse에서 employee 객체를 구성하는 함수
        HighPerformanceEmployee hpoEmployee = new HighPerformanceEmployee(
            adjustSubject.getId(),
            adjustSubject.getEmployee().getId(),
            adjustSubject.getEmployee().getEmpNum(),
            adjustSubject.getEmployee().getName(),
            adjustSubject.getEmployee().getDepartment().getName(),
            adjustSubject.getGrade().getName(),
            adjustSubject.getRank().getCode(),
            adjustSubject.getIsInHpo()
        );

        return hpoEmployee;
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
                    .isInHpo(changedHighPerformGroupEmployee.getIsInHpo())
                    .build();
                adjustSubjectRepository.save(saveAdjustSubject);
            });
    }

    public MainAdjPaybandBothSubjectsResponse getBothUpperLowerSubjects(Long adjInfoId) { //상한, 하한 초과자 가져오기
        return new MainAdjPaybandBothSubjectsResponse(getUpperSubjects(adjInfoId), getLowerSubjects(adjInfoId));
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getUpperSubjects(
        Long adjInfoId
    ) {     //상한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjustSubjectRepository.findAllAdjSubjectAndStdSalaryAndUpper(
            adjInfoId);

        return getMainAdjUpperPaybandSubjectsResponses(adjSubjectSalaryDtos);
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjects(
        Long adjInfoId
    ) {     //하한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjustSubjectRepository.findAllAdjSubjectAndStdSalaryAndLower(
            adjInfoId);

        return getAdjLowerPaybandSubjectsResponses(adjSubjectSalaryDtos);
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getAdjLowerPaybandSubjectsResponses(
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos) {
        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) < 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getName())
                    .gradeName(employee.getGrade().getName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public void modifyAdjustSubject(Long adjSubjectId, Boolean paybandUse, Double limitPrice) {
        AdjustSubject adjustSubject = adjustSubjectRepository.findById(adjSubjectId)
            .filter(adjustSubject1 -> !adjustSubject1.getDeleted())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        AdjustSubject saveAdjustSubject;
        if (paybandUse) {
            saveAdjustSubject = adjustSubject.toBuilder()
                .finalStdSalary(limitPrice)
                .isPaybandApplied(paybandUse)
                .build();
        } else {
            saveAdjustSubject = adjustSubject.toBuilder()
                .finalStdSalary(adjustSubject.getStdSalary())
                .isPaybandApplied(paybandUse)
                .build();
        }
        adjustSubjectRepository.save(saveAdjustSubject);
    }

    public MainAdjPaybandBothSubjectsResponse getBothUpperLowerSubjectsWithSearchKey(Long adjInfoId, String searchKey) {
        return new MainAdjPaybandBothSubjectsResponse(getUpperSubjectsWithSearchKey(adjInfoId, searchKey),
            getLowerSubjectsWithSearchKey(adjInfoId, searchKey));
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getUpperSubjectsWithSearchKey(
        Long adjInfoId, String searchKey
    ) {
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjustSubjectRepository.findAllAdjSubjectAndStdSalaryAndUpperWithSearchKey(
            adjInfoId, searchKey);

        return getMainAdjUpperPaybandSubjectsResponses(adjSubjectSalaryDtos);
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getMainAdjUpperPaybandSubjectsResponses(
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos) {
        return adjSubjectSalaryDtos.stream()
            .filter(adjSubjectSalaryDto -> {
                return adjSubjectSalaryDto.getStdSalary().compareTo(adjSubjectSalaryDto.getLimitPrice()) > 0;
            })
            .map(dto -> {
                Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                dto = dto.toBuilder()
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .depName(employee.getDepartment().getName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    private List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjectsWithSearchKey(
        Long adjInfoId, String searchKey
    ) {
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjustSubjectRepository.findAllAdjSubjectAndStdSalaryAndLowerWithSearchKey(
            adjInfoId, searchKey);
        return getAdjLowerPaybandSubjectsResponses(adjSubjectSalaryDtos);
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
