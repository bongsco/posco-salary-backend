package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.MainResultDto;
import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.response.CompensationEmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.api.adjust.annual.dto.response.MainResultResponses;
import com.bongsco.api.adjust.annual.dto.response.PreprocessAdjSubjectsResponse;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager em;

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

    public List<EmployeeResponse> findBySearchKey(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustIdAndEmployeeName(adjInfoId, searchKey);

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

    public List<EmployeeResponse> findOne(Long adjInfoId, String searchKey) {
        // 연봉조정차수&검색정보를 이용해 정기연봉조정대상자 테이블 가져오기
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjustIdAndEmployeeName(adjInfoId, searchKey);

        return subjects.stream().map(EmployeeResponse::from).toList();
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

    public MainResultResponses getFinalResult(Long adjustId, String filterEmpNum, String filterName,
        String filterGrade, String filterDepartment, String filterRank,
        List<Map<String, String>> sorts, Integer pageNumber, Integer pageSize) {

        Map<String, String> mapping = Map.of(
            "empNum", "e.empNum",
            "name", "e.name",
            "departmentName", "d.name",
            "gradeName", "g.name",
            "rankCode", "r.code",
            "stdSalary", "asj.finalStdSalary",
            "totalSalary", "asj.finalStdSalary+asj.hpoBonus"
        );

        // Base JPQL
        String baseQuery = """
                FROM AdjustSubject asj
                JOIN Employee e ON e.id = asj.employee.id 
                JOIN Grade g ON g.id = asj.grade.id
                JOIN Department d ON d.id = e.department.id
                JOIN Rank r ON r.id = asj.rank.id
                WHERE asj.adjust.id = :adjustId
                    AND e.empNum LIKE COALESCE(:filterEmpNum, e.empNum)
                    AND e.name LIKE COALESCE(:filterName, e.name)
                    AND g.name = COALESCE(:filterGrade, g.name)
                    AND d.name = COALESCE(:filterDepartment, d.name)
                    AND r.code = COALESCE(:filterRank, r.code)
            """;

        // Content JPQL
        String contentJpql = "SELECT new com.bongsco.api.adjust.annual.dto.MainResultDto(" +
            "e.empNum, e.name, g.name, e.positionName, d.name, r.code, " +
            "e.stdSalaryIncrementRate, asj.finalStdSalary, asj.stdSalary, " +
            "asj.hpoBonus, asj.isInHpo, e.id, asj.id, g.id, r.id) " + baseQuery;

        // Count JPQL
        String countJpql = "SELECT COUNT(asj.id) " + baseQuery;

        // 정렬
        if (sorts != null && !sorts.isEmpty()) {
            contentJpql += " ORDER BY " + sorts.stream()
                .map(sortMap -> {
                    Map.Entry<String, String> entry = sortMap.entrySet().iterator().next();
                    String key = mapping.get(entry.getKey());
                    String direction = "내림차순".equals(entry.getValue()) ? "DESC" : "ASC";
                    return key + " " + direction;
                })
                .collect(Collectors.joining(", "));
        }

        // count query
        Long total = em.createQuery(countJpql, Long.class)
            .setParameter("adjustId", adjustId)
            .setParameter("filterEmpNum", filterEmpNum == null ? null : "%" + filterEmpNum + "%")
            .setParameter("filterName", filterName == null ? null : "%" + filterName + "%")
            .setParameter("filterGrade", filterGrade)
            .setParameter("filterDepartment", filterDepartment)
            .setParameter("filterRank", filterRank)
            .getSingleResult();

        int totalPages = (int)Math.ceil((double)total / pageSize);
        int safePageNumber = Math.max(Math.min(pageNumber, totalPages - 1), 0);
        
        Pageable pageable = PageRequest.of(safePageNumber, pageSize);

        // content query
        List<MainResultDto> mainResultDtos = em.createQuery(contentJpql, MainResultDto.class)
            .setParameter("adjustId", adjustId)
            .setParameter("filterEmpNum", filterEmpNum == null ? null : "%" + filterEmpNum + "%")
            .setParameter("filterName", filterName == null ? null : "%" + filterName + "%")
            .setParameter("filterGrade", filterGrade)
            .setParameter("filterDepartment", filterDepartment)
            .setParameter("filterRank", filterRank)
            .setFirstResult((int)pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Adjust adjust = adjustRepository.findById(adjustId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        List<AdjustGrade> adjustGrades = adjustGradeRepository.findByAdjustId(adjustId);

        List<MainResultResponses.MainResultResponse> responseList = mainResultDtos.stream().map(dto -> {
            String paybandResult =
                dto.getStdSalary() == null || dto.getFinalStdSalary() == null ? "미적용" :
                    dto.getStdSalary() > dto.getFinalStdSalary() ? "적용(상한)" :
                        dto.getStdSalary() < dto.getFinalStdSalary() ? "적용(하한)" :
                            "미적용";

            AdjustGrade adjustGrade = adjustGrades.stream()
                .filter(a -> a.getGrade().getId() == dto.getGradeId())
                .findFirst().orElse(null);

            SalaryIncrementByRank salaryIncrementByRank = salaryIncrementByRankRepository
                .findByRankIdAndAdjustGradeId(dto.getRankId(), adjustGrade.getId())
                .orElse(null);

            Double bonusMultiplier = salaryIncrementByRank != null ? salaryIncrementByRank.getBonusMultiplier() : 0.0;
            Double salaryIncrementRate =
                salaryIncrementByRank != null ? salaryIncrementByRank.getSalaryIncrementRate() : 0.0;

            if (dto.getIsInHpo() != null && dto.getIsInHpo()) {
                bonusMultiplier += Optional.ofNullable(adjust.getHpoBonusMultiplier()).orElse(0.0);
                Double hpoSalaryIncrementRate = Optional.ofNullable(adjust.getHpoSalaryIncrementByRank()).orElse(0.0);
                salaryIncrementRate = ((1 + salaryIncrementRate / 100) * (1 + hpoSalaryIncrementRate / 100) - 1) * 100;
            }

            Double beforeFinalStdSalary = 0.0;
            Double beforeTotalSalary = 0.0;
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId, dto.getEmpId());
            if (beforeAdjustSubject != null) {
                beforeFinalStdSalary = Optional.ofNullable(beforeAdjustSubject.getFinalStdSalary()).orElse(0.0);
                beforeTotalSalary =
                    Optional.ofNullable(beforeAdjustSubject.getFinalStdSalary()).orElse(0.0) +
                        Optional.ofNullable(beforeAdjustSubject.getHpoBonus()).orElse(0.0);
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
                .stdSalaryIncrementRate(dto.getStdSalaryIncrementRate())
                .payband(paybandResult)
                .salaryBefore(beforeFinalStdSalary)
                .stdSalary(dto.getFinalStdSalary())
                .totalSalaryBefore(beforeTotalSalary)
                .totalSalary(
                    Optional.ofNullable(dto.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(dto.getHpoBonus())
                        .orElse(0.0))
                .build();
        }).toList();

        return new MainResultResponses(responseList, totalPages, safePageNumber + 1);
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
