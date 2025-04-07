package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.domain.RepresentativeSalary;
import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjResultResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.CompensationEmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.PreprocessAdjSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjSubjectRepository;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.adjust.repository.GradeRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankIncrementRateRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RepresentativeSalaryRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.user.domain.Employee;
import com.bongsco.poscosalarybackend.user.domain.Grade;
import com.bongsco.poscosalarybackend.user.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjSubjectService {
    private final AdjSubjectRepository adjSubjectRepository;
    private final RankIncrementRateRepository rankIncrementRateRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;
    private final EmployeeRepository employeeRepository;
    private final AdjustRepository adjustRepository;
    private final RepresentativeSalaryRepository representativeSalaryRepository;
    private final GradeRepository gradeRepository;
    private final AdjustService adjustService;

    public static Double calculateMedian(List<AdjSubject> adjSubjects) {
        if (adjSubjects.isEmpty()) {
            return 0.0;
        }

        List<AdjSubject> salaryPerGrade = adjSubjects.stream()
            .sorted(Comparator.comparing(AdjSubject::getFinalStdSalary))
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

        return getMainAdjUpperPaybandSubjectsResponses(adjSubjectSalaryDtos);
    }

    public List<MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse> getLowerSubjects(
        Long adjInfoId
    ) {     //하한초과자 가져오기
        List<AdjSubjectSalaryDto> adjSubjectSalaryDtos = adjSubjectRepository.findAllAdjSubjectAndStdSalaryAndLower(
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
                    .depName(employee.getDepartment().getDepName())
                    .gradeName(employee.getGrade().getGradeName())
                    .positionName(employee.getPositionName())
                    .build();
                return MainAdjPaybandBothSubjectsResponse.MainAdjPaybandSubjectsResponse.from(dto);
            })
            .toList();
    }

    public void modifyAdjustSubject(Long adjSubjectId, Boolean paybandUse, Double limitPrice) {
        AdjSubject adjSubject = adjSubjectRepository.findById(adjSubjectId)
            .filter(adjSubject1 -> !adjSubject1.getDeleted())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        AdjSubject saveAdjSubject;
        if (paybandUse) {
            saveAdjSubject = adjSubject.toBuilder().finalStdSalary(limitPrice).paybandUse(paybandUse).build();
        } else {
            saveAdjSubject = adjSubject.toBuilder()
                .finalStdSalary(adjSubject.getStdSalary())
                .paybandUse(paybandUse)
                .build();
        }
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
        return getAdjLowerPaybandSubjectsResponses(adjSubjectSalaryDtos);
    }

    public void calculateSalary(Long adjInfoId) {
        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        Double evalAnnualSalaryIncrement =
            Optional.ofNullable(adjInfo.getEvalAnnualSalaryIncrement()).orElse(0.0) / 100;
        //5%일때 5로 들어간다는 전제하에 이렇게 해놓음

        adjSubjects.stream().forEach(adjSubject -> {
            //기준연봉= 전년도 기준연봉+(직무기본연봉*평차등연봉인상률)

            //전년도 기준연봉 불러옴
            AdjSubject beforeAdjSubject = adjSubjectRepository.findBeforeAdjSubject(adjInfoId,
                adjSubject.getEmployee().getId());
            if (beforeAdjSubject == null)
                return; //신입 제외

            //직무 기본 연봉 불러옴
            Double gradeBaseSalary = adjSubject.getGrade().getGradeBaseSalary();

            //연봉 인상률 불러옴
            RankIncrementRate rankIncrementRate = rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(
                adjSubject.getRank().getId(), adjInfoId, adjSubject.getGrade().getId()).orElse(null);
            Double evalDiffIncrement =
                rankIncrementRate == null ? 0.0 : (rankIncrementRate.getEvalDiffIncrement() / 100);

            //고성과 있으면
            //평차등연봉인상률 = (직급, 평가별 평차연) * (1 + 고성과평차연)
            if (adjSubject.getInHighPerformGroup()) {
                evalDiffIncrement = evalDiffIncrement * (1 + evalAnnualSalaryIncrement);
            }

            Double newStdSalary =
                (beforeAdjSubject.getFinalStdSalary() == null ? 1000000 : beforeAdjSubject.getFinalStdSalary())
                    + gradeBaseSalary * evalDiffIncrement;

            AdjSubject saveAdjSubject = adjSubject.toBuilder()
                .stdSalary(newStdSalary)
                .finalStdSalary(newStdSalary)
                .build();
            adjSubjectRepository.save(saveAdjSubject);
        });
    }

    public void calculateAddPayment(Long adjInfoId) {

        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        Double evalPerformProvideRate =
            Optional.ofNullable(adjInfo.getEvalPerformProvideRate()).orElse(100.0) / 100;
        //400% 이런식으로 들어옴

        adjSubjects.stream().forEach(adjSubject -> {
            // 평가차등 경영 성과금 = 평차금기준임금(업적연봉기본지금) * 평가차등경영성과금지급률
            // 평차금기준임금 = ((기준연봉월할액 + 직무기본연봉) / 2 ) + 12만원

            // 직무기본연봉 가져옴
            Double gradeBaseSalary = adjSubject.getGrade().getGradeBaseSalary();
            Double rankBaseStandardSalary = (adjSubject.getFinalStdSalary() + gradeBaseSalary) / 12 + 12;

            //평가차등경영성과금지급률 가져옴
            RankIncrementRate rankIncrementRate = rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(
                adjSubject.getRank().getId(), adjInfoId, adjSubject.getGrade().getId()).orElse(null);
            Double evalDiffBonus = rankIncrementRate == null ? 0.0 : (rankIncrementRate.getEvalDiffBonus() / 100);

            //고성과 조직일 경우
            //평가차등경영성과금 지급률 = 평가차등경영성과금 지급률+고성과 경영성과금지급률
            if (adjSubject.getInHighPerformGroup()) {
                evalDiffBonus += evalPerformProvideRate;
            }

            Double newPerformAddPayment = (double)Math.round(rankBaseStandardSalary * evalDiffBonus);

            AdjSubject saveAdjSubject = adjSubject.toBuilder().performAddPayment(newPerformAddPayment).build();
            adjSubjectRepository.save(saveAdjSubject);
        });
    }

    public AdjResultResponse getFinalResult(Long adjInfoId) {
        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        return getAdjResultResponse(adjInfoId, adjSubjects);
    }

    public AdjResultResponse getFinalResultWithSearchKey(Long adjInfoId, String searchKey) {
        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_IdAndSearchKey(adjInfoId, searchKey)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        return getAdjResultResponse(adjInfoId, adjSubjects);
    }

    private AdjResultResponse getAdjResultResponse(Long adjInfoId, List<AdjSubject> adjSubjects) {
        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        List<PaybandCriteria> paybandCriterias = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(pc -> !pc.getDeleted())
            .toList();

        Long beforeAdjInfoId = adjustService.getBeforeAdjInfoId(adjInfoId);

        List<RepresentativeSalary> representativeSalaries = representativeSalaryRepository.findByAdjInfo_Id(
            beforeAdjInfoId);

        return new AdjResultResponse(adjSubjects.stream().map(adjSubject -> {
            Employee employee = adjSubject.getEmployee();
            //상한값 하한값 가져오기,..
            PaybandCriteria paybandCriteria = paybandCriterias.stream()
                .filter(pc -> Objects.equals(pc.getGrade().getId(), adjSubject.getGrade().getId()))
                .findFirst()
                .orElse(null);

            //전년도 기준연봉 불러옴
            AdjSubject beforeAdjSubject = adjSubjectRepository.findBeforeAdjSubject(adjInfoId,
                employee.getId());

            Double beforeFinalStdSalary = Optional.ofNullable(beforeAdjSubject.getFinalStdSalary()).orElse(0.0);
            Double finalStdSalary = Optional.ofNullable(adjSubject.getFinalStdSalary()).orElse(0.0);

            Double representativeVal = RepresentativeSalaryService.getRepresentativeVal(representativeSalaries,
                adjSubject.getGrade().getId());
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;

            return new AdjResultResponse.AdjResult(employee.getEmpNum(), employee.getName(), employee.getBirth(),
                employee.getHireDate(), employee.getStation().getStationName(), employee.getPaymentCriteria()
                .getPaymentName(), employee.getDepartment().getDepName(), employee.getPositionName(),
                adjSubject.getGrade().getGradeName(), employee.getPositionArea(),
                adjInfo.getEvalAnnualSalaryIncrement(), adjInfo.getEvalPerformProvideRate(),
                paybandCriteria != null ? paybandCriteria.getUpperLimitPrice() : null,
                paybandCriteria != null ? paybandCriteria.getLowerLimitPrice() : null, adjSubject.getStdSalary(),
                beforeAdjSubject.getAdjInfo().getYear(), beforeAdjSubject.getAdjInfo().getOrderNumber(), finalStdSalary,
                beforeAdjSubject.getPerformAddPayment(),
                Optional.ofNullable(beforeAdjSubject.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(
                    beforeAdjSubject.getPerformAddPayment()).orElse(0.0), representativeVal,
                adjInfo.getYear(), adjInfo.getOrderNumber(), finalStdSalaryIncrementRate,
                adjSubject.getFinalStdSalary(),
                adjSubject.getPerformAddPayment(),
                Optional.ofNullable(adjSubject.getFinalStdSalary()).orElse(0.0) + Optional.ofNullable(
                    adjSubject.getPerformAddPayment()).orElse(0.0));
        }).toList());
    }

    public void calculateRepresentativeVal(Long adjInfoId) {
        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        List<Grade> grades = gradeRepository.findAll();
        Map<Long, Double> representativeVal = adjSubjects.stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(adjSubject -> adjSubject.getFinalStdSalary() != null) //gradeId:대표값
            .collect(Collectors.groupingBy(
                s -> s.getGrade().getId(), // 1차 그룹화 (gradeId 기준)
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    AdjSubjectService::calculateMedian // 상위 5% 제외 후 중간값
                )
            ));

        representativeVal.entrySet().forEach(val -> {
            representativeSalaryRepository.save(RepresentativeSalary.builder()
                .adjInfo(adjInfo)
                .grade(grades.stream()
                    .filter(grade -> Objects.equals(grade.getId(), val.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND)))
                .representativeVal(val.getValue())
                .build());
        });
    }

    public void changeIncrementRate(Long adjInfoId) {
        List<AdjSubject> adjSubjects = adjSubjectRepository.findByAdjInfo_Id(adjInfoId)
            .stream()
            .filter(adjSubject -> !adjSubject.getDeleted())
            .filter(AdjSubject::getSubjectUse)
            .toList();

        adjSubjects.forEach(adjSubject -> {
            Employee employee = adjSubject.getEmployee();
            AdjSubject beforeAdjSubject = adjSubjectRepository.findBeforeAdjSubject(adjInfoId,
                employee.getId());

            Double beforeFinalStdSalary = beforeAdjSubject.getFinalStdSalary();
            Double finalStdSalary = adjSubject.getFinalStdSalary();
            if (beforeFinalStdSalary == null || finalStdSalary == null) {
                return;
            }
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;

            employeeRepository.save(employee.toBuilder().stdSalaryIncrementRate(finalStdSalaryIncrementRate).build());
        });
    }
}
