package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.request.PaybandCriteriaModifyRequest;
import com.bongsco.api.adjust.annual.dto.request.RankIncrementRateRequest;
import com.bongsco.api.adjust.annual.dto.request.SubjectCriteriaRequest;
import com.bongsco.api.adjust.annual.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.entity.SalaryIncrementRateByRank;
import com.bongsco.api.adjust.annual.repository.SalaryIncrementRateByRankRepository;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustEmploymentType;
import com.bongsco.api.adjust.common.entity.AdjustGrade;
import com.bongsco.api.adjust.common.repository.AdjustEmploymentTypeRepository;
import com.bongsco.api.adjust.common.repository.AdjustGradeRepository;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.GradeRepository;
import com.bongsco.api.adjust.common.repository.PaybandCriteriaRepository;
import com.bongsco.api.adjust.common.repository.RankRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.common.exception.ErrorCode;
import com.bongsco.api.employee.entity.EmploymentType;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;
import com.bongsco.api.employee.repository.EmploymentTypeRepository;

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
                grade.getGradeName(),
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
                grade.getGradeName(),
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

    @Transactional
    public List<SalaryIncrementRateByRank> saveRankIncrementRates(Long adjInfoId, RankIncrementRateRequest request) {

        Adjust existingAdjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Adjust updatedAdjust = existingAdjust.toBuilder()
            .salaryIncrementRateByRank(request.getEvalDiffIncrementPromoted())
            .hpoBonusMultiplier(request.getEvalDiffBonusPromoted())
            .build();

        // 모든 rank_id -> grade_id -> 상세 정보 매핑
        List<SalaryIncrementRateByRank> salaryIncrementRateByRanks = request.getRankData().entrySet().stream()
            .flatMap(rankEntry -> {
                Long rankId = rankEntry.getKey();
                Rank rank = rankRepository.findById(rankId)
                    .orElseThrow(() -> new IllegalArgumentException("Rank not found with ID: " + rankId));

                return rankEntry.getValue().entrySet().stream().map(gradeEntry -> {
                    Long gradeId = gradeEntry.getKey();
                    Grade grade = gradeRepository.findById(gradeId)
                        .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));

                    return SalaryIncrementRateByRank.builder()
                        .adjust(updatedAdjust)
                        .rank(rank)
                        .grade(grade)
                        .evalDiffBonus(gradeEntry.getValue().getEvalDiffBonus())
                        .evalDiffIncrement(gradeEntry.getValue().getEvalDiffIncrement())
                        .build();
                });
            })
            .collect(Collectors.toList());

        return salaryIncrementRateByRankRepository.saveAll(salaryIncrementRateByRanks);
    }

    @Transactional
    public List<SalaryIncrementRateByRank> updateRankIncrementRates(Long adjInfoId, RankIncrementRateRequest request) {
        Adjust existingAdjust = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Adjust updatedAdjust = existingAdjust.toBuilder()
            .salaryIncrementRateByRank(request.getEvalDiffIncrementPromoted())
            .hpoBonusMultiplier(request.getEvalDiffBonusPromoted())
            .build();

        List<SalaryIncrementRateByRank> updatedSalaryIncrementRateByRanks = request.getRankData().entrySet().stream()
            .flatMap(rankEntry -> {
                Long rankId = rankEntry.getKey();
                Rank rank = rankRepository.findById(rankId)
                    .orElseThrow(() -> new IllegalArgumentException("Rank not found with ID: " + rankId));

                return rankEntry.getValue().entrySet().stream().map(gradeEntry -> {
                    Long gradeId = gradeEntry.getKey();
                    Grade grade = gradeRepository.findById(gradeId)
                        .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));

                    RankIncrementRateRequest.RankIncrementRateDetail detail = gradeEntry.getValue();

                    // 기존 데이터 존재 여부 확인
                    Optional<SalaryIncrementRateByRank> existingRecord =
                        salaryIncrementRateByRankRepository.findByRankIdAndAdjustIdAndGradeId(rankId, adjInfoId,
                            gradeId);

                    if (existingRecord.isPresent()) {
                        // 기존 데이터 업데이트
                        SalaryIncrementRateByRank existingRate = existingRecord.get();
                        existingRate = existingRate.toBuilder()
                            .evalDiffBonus(detail.getEvalDiffBonus())
                            .evalDiffIncrement(detail.getEvalDiffIncrement())
                            .build();
                        return existingRate;
                    } else {
                        // 새로운 데이터 생성
                        return SalaryIncrementRateByRank.builder()
                            .adjust(updatedAdjust)
                            .rank(rank)
                            .grade(grade)
                            .evalDiffBonus(detail.getEvalDiffBonus())
                            .evalDiffIncrement(detail.getEvalDiffIncrement())
                            .build();
                    }
                });
            })
            .collect(Collectors.toList());

        return salaryIncrementRateByRankRepository.saveAll(updatedSalaryIncrementRateByRanks);
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
