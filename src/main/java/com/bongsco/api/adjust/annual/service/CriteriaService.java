package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.bongsco.api.adjust.annual.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.api.adjust.annual.dto.response.PaymentRateResponse;
import com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;
import com.bongsco.api.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementRateByRankRepository;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustEmploymentType;
import com.bongsco.api.adjust.common.entity.AdjustGrade;
import com.bongsco.api.adjust.common.repository.AdjustEmploymentTypeRepository;
import com.bongsco.api.adjust.common.repository.AdjustGradeRepository;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.common.exception.ErrorCode;
import com.bongsco.api.employee.entity.EmploymentType;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;
import com.bongsco.api.employee.repository.EmploymentTypeRepository;
import com.bongsco.api.employee.repository.GradeRepository;
import com.bongsco.api.employee.repository.RankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriteriaService {
    private final AdjustRepository adjustRepository;
    private final AdjustGradeRepository adjustGradeRepository;
    private final AdjustEmploymentTypeRepository adjustEmploymentTypeRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final GradeRepository gradeRepository;
    private final RankRepository rankRepository;
    private final SalaryIncrementRateByRankRepository salaryIncrementRateByRankRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;

    public SubjectCriteriaResponse getSubjectCriteria(Long adjInfoId) {
        Adjust info = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Set<Long> selectedGradeIds = adjustGradeRepository.findByAdjustId(adjInfoId).stream()
            .map(link -> link.getGrade().getId())
            .collect(Collectors.toSet());

        Set<Long> selectedPaymentIds = adjustEmploymentTypeRepository.findByAdjustId(adjInfoId).stream()
            .map(link -> link.getEmploymentType().getId())
            .collect(Collectors.toSet());

        List<SubjectCriteriaResponse.SelectableItemDto> gradeDtos = gradeRepository.findAll().stream()
            .map(grade -> new SubjectCriteriaResponse.SelectableItemDto(
                grade.getId(),
                grade.getName(),
                selectedGradeIds.contains(grade.getId())
            )).toList();

        List<SubjectCriteriaResponse.SelectableItemDto> paymentDtos = employmentTypeRepository.findAll().stream()
            .map(payment -> new SubjectCriteriaResponse.SelectableItemDto(
                payment.getId(),
                payment.getName(),
                selectedPaymentIds.contains(payment.getId())
            )).toList();

        return new SubjectCriteriaResponse(
            info.getBaseDate(),
            info.getExceptionStartDate(),
            info.getExceptionEndDate(),
            gradeDtos,
            paymentDtos
        );
    }

    @Transactional
    public SubjectCriteriaResponse updateSubjectCriteria(Long adjInfoId, SubjectCriteriaRequest request) {
        Adjust adjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        // 날짜 업데이트
        adjust = adjust.toBuilder()
            .baseDate(request.getBaseDate())
            .exceptionStartDate(request.getExpStartDate())
            .exceptionEndDate(request.getExpEndDate())
            .build();
        adjustRepository.save(adjust);

        // 등급 처리
        Map<Long, AdjustGrade> existingGrades = adjustGradeRepository.findByAdjustId(adjInfoId).stream()
            .collect(Collectors.toMap(g -> g.getGrade().getId(), Function.identity()));

        for (Map.Entry<Long, Boolean> entry : request.getGradeSelections().entrySet()) {
            Long id = entry.getKey();
            boolean checked = entry.getValue();
            boolean exists = existingGrades.containsKey(id);

            if (checked && !exists) {
                Grade grade = gradeRepository.findById(id)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
                adjustGradeRepository.save(
                    AdjustGrade.builder().adjust(adjust).grade(grade).build()
                );
            } else if (!checked && exists) {
                adjustGradeRepository.delete(existingGrades.get(id));
            }
        }

        // 직급 처리
        Map<Long, AdjustEmploymentType> existingPayments = adjustEmploymentTypeRepository.findByAdjustId(adjInfoId)
            .stream()
            .collect(Collectors.toMap(p -> p.getEmploymentType().getId(), Function.identity()));

        for (Map.Entry<Long, Boolean> entry : request.getPaymentSelections().entrySet()) {
            Long id = entry.getKey();
            boolean checked = entry.getValue();
            boolean exists = existingPayments.containsKey(id);

            if (checked && !exists) {
                EmploymentType pc = employmentTypeRepository.findById(id)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
                adjustEmploymentTypeRepository.save(
                    AdjustEmploymentType.builder().adjust(adjust).employmentType(pc).build()
                );
            } else if (!checked && exists) {
                adjustEmploymentTypeRepository.delete(existingPayments.get(id));
            }
        }

        // ✅ 수정된 정보 기반으로 다시 전체 목록 + 체크여부 포함 응답 생성
        Set<Long> selectedGradeIds = adjustGradeRepository.findByAdjustId(adjInfoId).stream()
            .map(link -> link.getGrade().getId())
            .collect(Collectors.toSet());

        Set<Long> selectedPaymentIds = adjustEmploymentTypeRepository.findByAdjustId(adjInfoId).stream()
            .map(link -> link.getEmploymentType().getId())
            .collect(Collectors.toSet());

        List<SubjectCriteriaResponse.SelectableItemDto> gradeDtos = gradeRepository.findAll().stream()
            .map(grade -> new SubjectCriteriaResponse.SelectableItemDto(
                grade.getId(),
                grade.getName(),
                selectedGradeIds.contains(grade.getId())
            )).toList();

        List<SubjectCriteriaResponse.SelectableItemDto> paymentDtos = employmentTypeRepository.findAll().stream()
            .map(payment -> new SubjectCriteriaResponse.SelectableItemDto(
                payment.getId(),
                payment.getName(),
                selectedPaymentIds.contains(payment.getId())
            )).toList();

        return new SubjectCriteriaResponse(
            adjust.getBaseDate(),
            adjust.getExceptionStartDate(),
            adjust.getExceptionEndDate(),
            gradeDtos,
            paymentDtos
        );
    }

    public PaymentRateResponse getPaymentRate(Long adjustId, List<String> gradeList) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        List<SalaryIncrementByRank> rates =
            salaryIncrementRateByRankRepository.findByAdjustIdAndGradeNames(adjustId, gradeList);

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
            .hpoSalaryIncrementRate(adjust.getHpoSalaryIncrementRateByRank())
            .hpoExtraBonusMultiplier(adjust.getHpoBonusMultiplier())
            .rank_rate(rankRateMap)
            .build();
    }

    @Transactional
    public List<String> updatePaymentRate(Long adjustId, PaymentRateUpdateRequest request) {
        // 기존 adjust 조회
        Adjust existingAdjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        // 기존 adjust 기반으로 새로운 Adjust 객체 생성
        Adjust updatedAdjust = existingAdjust.toBuilder()
            .id(adjustId)
            .hpoSalaryIncrementRateByRank(request.getHpoSalaryIncrementRate())
            .hpoBonusMultiplier(request.getHpoExtraBonusMultiplier())
            .build();

        // 새로운 Adjust 저장
        adjustRepository.save(updatedAdjust);

        List<String> updatedGrades = new ArrayList<>();

        for (Map.Entry<String, Map<String, PaymentRateUpdateRequest.PaymentRateValue>> gradeEntry : request.getRank_rate()
            .entrySet()) {
            String gradeName = gradeEntry.getKey(); // P1

            Grade grade = gradeRepository.findByName(gradeName)
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

            AdjustGrade adjustGrade = adjustGradeRepository.findByAdjustIdAndGradeId(adjustId, grade.getId())
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

            for (Map.Entry<String, PaymentRateUpdateRequest.PaymentRateValue> evalEntry : gradeEntry.getValue()
                .entrySet()) {
                String evalCode = evalEntry.getKey(); // S, A, ...
                Rank rank = rankRepository.findByCode(evalCode)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

                PaymentRateUpdateRequest.PaymentRateValue values = evalEntry.getValue();

                SalaryIncrementByRank entity = salaryIncrementRateByRankRepository
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

                salaryIncrementRateByRankRepository.save(entity);
            }

            updatedGrades.add(gradeName);
        }

        return updatedGrades;
    }

    @Transactional
    public PaybandCriteriaConfigListResponse getPaybandCriteria(Long adjInfoId) {

        List<PaybandCriteria> existingPaybandCriteriaList = paybandCriteriaRepository.findByAdjustId(adjInfoId);
        Set<Long> existingGradeIdSet = existingPaybandCriteriaList.stream()
            .map(pc -> pc.getGrade().getId())
            .collect(Collectors.toSet());

        List<Grade> grades = gradeRepository.findAll();
        List<Grade> leftGrades = grades.stream()
            .filter(grade -> !existingGradeIdSet.contains(grade.getId()))
            .collect(Collectors.toList());

        Adjust adjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid adjInfoId: " + adjInfoId));

        List<PaybandCriteria> newPaybandCriteriaList = leftGrades.stream()
            .map(grade ->
                PaybandCriteria.builder()
                    .grade(grade)
                    .adjust(adjust)
                    .upperBound(130.0)
                    .lowerBound(70.0)
                    .build()
            ).collect(Collectors.toList());

        paybandCriteriaRepository.saveAll(newPaybandCriteriaList);

        existingPaybandCriteriaList.addAll(newPaybandCriteriaList);

        return PaybandCriteriaConfigListResponse.from(existingPaybandCriteriaList);
    }

    @Transactional
    public List<PaybandCriteria> updatePaybandCriteria(PaybandCriteriaModifyRequest request) {
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

        return paybandCriteriaRepository.saveAll(updatedPaybandCriteriaList);
    }
}
