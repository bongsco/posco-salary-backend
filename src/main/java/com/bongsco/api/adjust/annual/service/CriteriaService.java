package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.PaymentRateDto;
import com.bongsco.api.adjust.annual.dto.request.PaybandCriteriaModifyRequest;
import com.bongsco.api.adjust.annual.dto.request.PaymentRateUpdateRequest;
import com.bongsco.api.adjust.annual.dto.request.SubjectCriteriaRequest;
import com.bongsco.api.adjust.annual.dto.response.EmployeeSimple;
import com.bongsco.api.adjust.annual.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.api.adjust.annual.dto.response.PaymentRateResponse;
import com.bongsco.api.adjust.annual.dto.response.SelectableItemDto;
import com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustEmploymentType;
import com.bongsco.api.adjust.common.entity.AdjustGrade;
import com.bongsco.api.adjust.common.entity.AdjustSubject;
import com.bongsco.api.adjust.common.repository.AdjustEmploymentTypeRepository;
import com.bongsco.api.adjust.common.repository.AdjustGradeRepository;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.AdjustSubjectRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.common.exception.ErrorCode;
import com.bongsco.api.employee.entity.Employee;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;
import com.bongsco.api.employee.repository.EmployeeRepository;
import com.bongsco.api.employee.repository.EmploymentTypeRepository;
import com.bongsco.api.employee.repository.GradeRepository;
import com.bongsco.api.employee.repository.RankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriteriaService {
    private final AdjustRepository adjustRepository;
    private final AdjustGradeRepository adjustGradeRepository;
    private final AdjustSubjectRepository adjustSubjectRepository;
    private final AdjustEmploymentTypeRepository adjustEmploymentTypeRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final GradeRepository gradeRepository;
    private final RankRepository rankRepository;
    private final SalaryIncrementByRankRepository salaryIncrementByRankRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;

    public SubjectCriteriaResponse getSubjectCriteria(Long adjustId) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        // 1. AdjustGrade -> SelectableItemDto 변환
        List<SelectableItemDto> gradeDtos = adjustGradeRepository.findByAdjustId(adjustId).stream()
            .map(ag -> new SelectableItemDto(
                ag.getGrade().getId(),
                ag.getGrade().getName(),
                ag.getIsActive()
            ))
            .collect(Collectors.toList());

        // 2. AdjustEmploymentType -> SelectableItemDto 변환
        List<SelectableItemDto> paymentDtos = adjustEmploymentTypeRepository.findByAdjustId(adjustId).stream()
            .map(ae -> new SelectableItemDto(
                ae.getEmploymentType().getId(),
                ae.getEmploymentType().getName(),
                ae.getIsActive()
            ))
            .collect(Collectors.toList());

        // 3. 최종 응답 구성
        return new SubjectCriteriaResponse(
            adjust.getBaseDate(),
            adjust.getExceptionStartDate(),
            adjust.getExceptionEndDate(),
            gradeDtos,
            paymentDtos
        );
    }

    @Transactional
    public SubjectCriteriaResponse updateSubjectCriteria(Long adjustId, SubjectCriteriaRequest request) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        // ✅ Adjust 정보 업데이트 (기존 객체 수정)
        Adjust updatedAdjust = adjust.toBuilder()
            .baseDate(request.getBaseDate())
            .exceptionStartDate(request.getExceptionStartDate())
            .exceptionEndDate(request.getExceptionEndDate())
            .build();

        adjustRepository.save(updatedAdjust);

        // ✅ 등급 체크 상태 반영
        Map<Long, AdjustGrade> gradeMap = adjustGradeRepository.findByAdjustId(adjustId).stream()
            .collect(Collectors.toMap(g -> g.getGrade().getId(), Function.identity()));

        request.getGradeSelections().forEach((gradeId, isChecked) -> {
            AdjustGrade ag = gradeMap.get(gradeId);
            if (ag != null && !isChecked.equals(ag.getIsActive())) {
                ag.setIsActive(isChecked);
            }
        });

        // ✅ 직급 체크 상태 반영
        Map<Long, AdjustEmploymentType> typeMap = adjustEmploymentTypeRepository.findByAdjustId(adjustId).stream()
            .collect(Collectors.toMap(t -> t.getEmploymentType().getId(), Function.identity()));

        request.getPaymentSelections().forEach((typeId, isChecked) -> {
            AdjustEmploymentType ae = typeMap.get(typeId);
            if (ae != null && !isChecked.equals(ae.getIsActive())) {
                ae.setIsActive(isChecked);
            }
        });

        // ✅ AdjustSubject 갱신
        // ✅ 선택된 등급/직급 ID 조회
        Set<Long> selectedGradeIds = adjustGradeRepository.findByAdjustIdAndIsActive(adjustId, true).stream()
            .map(g -> g.getGrade().getId())
            .collect(Collectors.toSet());

        Set<Long> selectedPaymentIds = adjustEmploymentTypeRepository.findByAdjustIdAndIsActive(adjustId, true).stream()
            .map(p -> p.getEmploymentType().getId())
            .collect(Collectors.toSet());

        // ✅ 기존 대상자 직원 ID만 조회 (최적화된 쿼리 사용)
        Set<Long> existingEmpIds = adjustSubjectRepository.findEmployeeIdsByAdjustId(adjustId);

        // ✅ 조건에 맞는 직원만 조회 (간단 projection)
        List<EmployeeSimple> matchingEmployees = employeeRepository.findSimpleEmployeesByCriteria(
            selectedGradeIds, selectedPaymentIds
        );

        // Map으로 변환 (id → EmployeeSimple)
        Map<Long, EmployeeSimple> empMap = matchingEmployees.stream()
            .collect(Collectors.toMap(EmployeeSimple::getId, Function.identity()));

        // ✅ 새롭게 추가해야 할 직원 ID
        Set<Long> newEmpIds = empMap.keySet();

        // ✅ 삭제 대상: 기존엔 있었지만, 새 목록엔 없음
        Set<Long> deleteEmpIds = new HashSet<>(existingEmpIds);
        deleteEmpIds.removeAll(newEmpIds);

        // ✅ 추가 대상: 새 목록에는 있는데 기존엔 없었음
        Set<Long> insertEmpIds = new HashSet<>(newEmpIds);
        insertEmpIds.removeAll(existingEmpIds);

        // ✅ 삭제 대상 생성
        List<AdjustSubject> toDeleteSubjects = deleteEmpIds.stream()
            .map(id -> AdjustSubject.builder()
                .adjust(adjust)
                .employee(Employee.builder().id(id).build())
                .build())
            .toList();

        // ✅ 추가 대상 생성
        List<AdjustSubject> toInsertSubjects = insertEmpIds.stream()
            .map(id -> {
                EmployeeSimple emp = empMap.get(id);
                return AdjustSubject.builder()
                    .adjust(adjust)
                    .employee(Employee.builder().id(id).build())
                    .grade(emp.getGrade())
                    .rank(emp.getRank())
                    .isSubject(true)
                    .isInHpo(false)
                    .isPaybandApplied(null)
                    .stdSalary(null)
                    .hpoBonus(null)
                    .finalStdSalary(null)
                    .build();
            })
            .toList();

        adjustSubjectRepository.deleteAll(toDeleteSubjects);
        adjustSubjectRepository.saveAll(toInsertSubjects);

        // ✅ 응답 DTO 구성
        List<SelectableItemDto> gradeDtos = gradeMap.values().stream()
            .map(ag -> new SelectableItemDto(
                ag.getGrade().getId(),
                ag.getGrade().getName(),
                Boolean.TRUE.equals(ag.getIsActive())
            ))
            .collect(Collectors.toList());

        List<SelectableItemDto> paymentDtos = typeMap.values().stream()
            .map(ae -> new SelectableItemDto(
                ae.getEmploymentType().getId(),
                ae.getEmploymentType().getName(),
                Boolean.TRUE.equals(ae.getIsActive())
            ))
            .collect(Collectors.toList());

        return new SubjectCriteriaResponse(
            adjust.getBaseDate(),
            adjust.getExceptionStartDate(),
            adjust.getExceptionEndDate(),
            gradeDtos,
            paymentDtos
        );
    }

    public PaymentRateResponse getPaymentRate(Long adjustId) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        List<AdjustGrade> criteriaList = adjustGradeRepository.findByAdjustIdAndIsActiveTrue(adjustId);

        List<String> gradeList = criteriaList.stream()
            .map(c -> c.getGrade().getName()) // "P1", "P2", ...
            .toList();

        System.out.println("📌 gradeList: " + gradeList);

        List<SalaryIncrementByRank> rates =
            salaryIncrementByRankRepository.findByAdjustIdAndGradeNames(adjustId, gradeList);

        Map<String, Map<String, PaymentRateDto>> rankRateMap = new HashMap<>();

        for (SalaryIncrementByRank rate : rates) {
            String grade = rate.getAdjustGrade().getGrade().getName(); // "P1", "P2"
            String evalGrade = rate.getRank().getCode();               // "S", "A"

            rankRateMap
                .computeIfAbsent(grade, g -> new HashMap<>())
                .put(evalGrade, new PaymentRateDto(
                    rate.getSalaryIncrementRate(),
                    rate.getBonusMultiplier()
                ));
        }

        return PaymentRateResponse.builder()
            .hpoSalaryIncrementRate(adjust.getHpoSalaryIncrementByRank())
            .hpoExtraBonusMultiplier(adjust.getHpoBonusMultiplier())
            .paymentRates(rankRateMap)
            .build();
    }

    @Transactional
    public List<String> updatePaymentRate(Long adjustId, PaymentRateUpdateRequest request) {
        Adjust existingAdjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Adjust updatedAdjust = existingAdjust.toBuilder()
            .id(adjustId)
            .hpoSalaryIncrementByRank(request.getHpoSalaryIncrementRate())
            .hpoBonusMultiplier(request.getHpoExtraBonusMultiplier())
            .build();

        adjustRepository.save(updatedAdjust);

        List<String> updatedGrades = new ArrayList<>();
        List<SalaryIncrementByRank> entitiesToSave = new ArrayList<>();

        for (Map.Entry<String, Map<String, PaymentRateUpdateRequest.PaymentRateValue>> gradeEntry : request.getPaymentRates()
            .entrySet()) {
            String gradeName = gradeEntry.getKey();

            Grade grade = gradeRepository.findByName(gradeName)
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

            AdjustGrade adjustGrade = adjustGradeRepository.findByAdjustIdAndGradeId(adjustId, grade.getId())
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

            for (Map.Entry<String, PaymentRateUpdateRequest.PaymentRateValue> evalEntry : gradeEntry.getValue()
                .entrySet()) {
                String evalCode = evalEntry.getKey();
                Rank rank = rankRepository.findByCode(evalCode)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

                PaymentRateUpdateRequest.PaymentRateValue values = evalEntry.getValue();

                SalaryIncrementByRank entity = salaryIncrementByRankRepository
                    .findByRankIdAndAdjustGradeId(rank.getId(), adjustGrade.getId())
                    .orElse(SalaryIncrementByRank.builder()
                        .rank(rank)
                        .adjustGrade(adjustGrade)
                        .build());

                entity = entity.toBuilder()
                    .id(entity.getId())
                    .salaryIncrementRate(values.getIncrementRate())
                    .bonusMultiplier(values.getBonusMultiplier())
                    .build();

                entitiesToSave.add(entity);
            }

            updatedGrades.add(gradeName);
        }

        salaryIncrementByRankRepository.saveAll(entitiesToSave);

        return updatedGrades;
    }

    @Transactional
    public PaybandCriteriaConfigListResponse getPaybandCriteria(Long adjustId) {
        List<PaybandCriteria> existingPaybandCriteriaList = paybandCriteriaRepository.findByAdjustIdAndIsActiveTrue(
            adjustId);

        return PaybandCriteriaConfigListResponse.from(existingPaybandCriteriaList);
    }

    @Transactional
    public void updatePaybandCriteria(PaybandCriteriaModifyRequest request) {
        List<PaybandCriteria> updatedPaybandCriteriaList = request.getPaybandCriteriaModifyDetailList()
            .stream()
            .map(paybandCriteriaModifyDetail -> {
                PaybandCriteria paybandCriteria = paybandCriteriaRepository.findById(
                        paybandCriteriaModifyDetail.getId())
                    .orElseThrow(
                        () -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
                return paybandCriteria
                    .toBuilder()
                    .upperBound(paybandCriteriaModifyDetail.getUpperBound())
                    .lowerBound(paybandCriteriaModifyDetail.getLowerBound())
                    .build();
            })
            .collect(Collectors.toList());

        paybandCriteriaRepository.saveAll(updatedPaybandCriteriaList);
    }
}