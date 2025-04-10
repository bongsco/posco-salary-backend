package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.AdjustSubjectIncrementDto;
import com.bongsco.api.adjust.annual.dto.AdjustSubjectSalaryDto;
import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjResultResponse;
import com.bongsco.api.adjust.annual.dto.response.CompensationEmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.api.adjust.annual.dto.response.PreprocessAdjSubjectsResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustSubject;
import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
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
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjust_Id(adjInfoId);

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
        List<AdjustSubject> subjects = adjustSubjectRepository.findByAdjust_Id(adjInfoId);

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

    @Transactional
    public void calculateSalaryAndBonus(Long adjustId) {
        List<AdjustSubjectSalaryDto> adjustSubjectSalaryDtos = adjustSubjectRepository.findDtoByAdjustId(adjustId);

        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Double hpoSalaryIncrementByRank =
            Optional.ofNullable(adjust.getHpoSalaryIncrementByRank()).orElse(0.0) / 100;
        //5%일때 5로 들어간다는 전제하에 이렇게 해놓음

        Double hpoBonusMultiplier = Optional.ofNullable(adjust.getHpoBonusMultiplier()).orElse(0.0);

        adjustSubjectSalaryDtos.stream().forEach(adjustSubjectDto -> {
            //기준연봉= 전년도 기준연봉+(직무기본연봉*평차등연봉인상률)

            //전년도 기준연봉 불러옴
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                adjustSubjectDto.getEmpId());
            if (beforeAdjustSubject == null)
                return; //신입 제외

            //직무 기본 연봉 불러옴
            Double gradeBaseSalary = adjustSubjectDto.getBaseSalary();

            //연봉 인상률 불러옴
            Double evalDiffIncrement = adjustSubjectDto.getSalaryIncrementRate() / 100 + 1;

            //고성과 있으면
            //평차등연봉인상률 = (직급, 평가별 평차연) * (1 + 고성과평차연)
            if (adjustSubjectDto.getIsInHpo()) {
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
            if (adjustSubjectDto.getIsInHpo()) {
                bonusMultiplier += hpoBonusMultiplier / 100;
            }

            Double newHpoBonus = (double)Math.round(rankBaseStandardSalary * bonusMultiplier);

            newHpoBonus = Math.floor(newHpoBonus / 1000) * 1000;

            adjustSubjectRepository.saveById(adjustSubjectDto.getAdjustSubjectId(), newStdSalary, newHpoBonus);
        });
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
        List<AdjustSubjectIncrementDto> adjustSubjectIncrementDtos = adjustSubjectRepository.findAdjustSubjectIncrementDtoByAdjustId(
            adjustId);

        adjustSubjectIncrementDtos.forEach(adjustSubjectIncrementDto -> {
            AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId,
                adjustSubjectIncrementDto.getEmpId());
            if (beforeAdjustSubject == null)
                return; //신입 제외

            Double beforeFinalStdSalary = beforeAdjustSubject.getFinalStdSalary();
            Double finalStdSalary = adjustSubjectIncrementDto.getFinalStdSalary();
            if (beforeFinalStdSalary == null || finalStdSalary == null) {
                return;
            }
            Double finalStdSalaryIncrementRate = ((finalStdSalary - beforeFinalStdSalary) / beforeFinalStdSalary) * 100;

            employeeRepository.changeIncrementRateById(adjustSubjectIncrementDto.getEmpId(),
                finalStdSalaryIncrementRate);
        });
    }
}
