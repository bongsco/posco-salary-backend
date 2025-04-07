package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.GradeAdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.PaymentAdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.PaymentCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.dto.request.PaybandCriteriaDeleteRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.PaybandCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.RankIncrementRateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.SubjectCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.SubjectCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.adjust.repository.GradeAdjInfoRepository;
import com.bongsco.poscosalarybackend.adjust.repository.GradeRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaymentAdjInfoRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaymentCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankIncrementRateRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.user.domain.Grade;
import com.bongsco.poscosalarybackend.user.domain.Rank;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CriteriaService {

    private final AdjustRepository adjustRepository;
    private final GradeAdjInfoRepository gradeAdjInfoRepository;
    private final PaymentAdjInfoRepository paymentAdjInfoRepository;
    private final PaymentCriteriaRepository paymentCriteriaRepository;
    private final GradeRepository gradeRepository;
    private final RankRepository rankRepository;
    private final RankIncrementRateRepository rankIncrementRateRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;

    public SubjectCriteriaResponse getSubjectCriteria(Long adjInfoId) {
        AdjInfo info = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Set<Long> selectedGradeIds = gradeAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .map(link -> link.getGrade().getId())
            .collect(Collectors.toSet());

        Set<Long> selectedPaymentIds = paymentAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .map(link -> link.getPaymentCriteria().getId())
            .collect(Collectors.toSet());

        List<SubjectCriteriaResponse.SelectableItemDto> gradeDtos = gradeRepository.findAll().stream()
            .map(grade -> new SubjectCriteriaResponse.SelectableItemDto(
                grade.getId(),
                grade.getGradeName(),
                selectedGradeIds.contains(grade.getId())
            )).toList();

        List<SubjectCriteriaResponse.SelectableItemDto> paymentDtos = paymentCriteriaRepository.findAll().stream()
            .map(payment -> new SubjectCriteriaResponse.SelectableItemDto(
                payment.getId(),
                payment.getPaymentName(),
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
        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        // 날짜 업데이트
        adjInfo = adjInfo.toBuilder()
            .baseDate(request.getBaseDate())
            .exceptionStartDate(request.getExpStartDate())
            .exceptionEndDate(request.getExpEndDate())
            .build();
        adjustRepository.save(adjInfo);

        // 등급 처리
        Map<Long, GradeAdjInfo> existingGrades = gradeAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .collect(Collectors.toMap(g -> g.getGrade().getId(), Function.identity()));

        for (Map.Entry<Long, Boolean> entry : request.getGradeSelections().entrySet()) {
            Long id = entry.getKey();
            boolean checked = entry.getValue();
            boolean exists = existingGrades.containsKey(id);

            if (checked && !exists) {
                Grade grade = gradeRepository.findById(id)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
                gradeAdjInfoRepository.save(
                    GradeAdjInfo.builder().adjInfo(adjInfo).grade(grade).build()
                );
            } else if (!checked && exists) {
                gradeAdjInfoRepository.delete(existingGrades.get(id));
            }
        }

        // 직급 처리
        Map<Long, PaymentAdjInfo> existingPayments = paymentAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .collect(Collectors.toMap(p -> p.getPaymentCriteria().getId(), Function.identity()));

        for (Map.Entry<Long, Boolean> entry : request.getPaymentSelections().entrySet()) {
            Long id = entry.getKey();
            boolean checked = entry.getValue();
            boolean exists = existingPayments.containsKey(id);

            if (checked && !exists) {
                PaymentCriteria pc = paymentCriteriaRepository.findById(id)
                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
                paymentAdjInfoRepository.save(
                    PaymentAdjInfo.builder().adjInfo(adjInfo).paymentCriteria(pc).build()
                );
            } else if (!checked && exists) {
                paymentAdjInfoRepository.delete(existingPayments.get(id));
            }
        }

        // ✅ 수정된 정보 기반으로 다시 전체 목록 + 체크여부 포함 응답 생성
        Set<Long> selectedGradeIds = gradeAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .map(link -> link.getGrade().getId())
            .collect(Collectors.toSet());

        Set<Long> selectedPaymentIds = paymentAdjInfoRepository.findByAdjInfoId(adjInfoId).stream()
            .map(link -> link.getPaymentCriteria().getId())
            .collect(Collectors.toSet());

        List<SubjectCriteriaResponse.SelectableItemDto> gradeDtos = gradeRepository.findAll().stream()
            .map(grade -> new SubjectCriteriaResponse.SelectableItemDto(
                grade.getId(),
                grade.getGradeName(),
                selectedGradeIds.contains(grade.getId())
            )).toList();

        List<SubjectCriteriaResponse.SelectableItemDto> paymentDtos = paymentCriteriaRepository.findAll().stream()
            .map(payment -> new SubjectCriteriaResponse.SelectableItemDto(
                payment.getId(),
                payment.getPaymentName(),
                selectedPaymentIds.contains(payment.getId())
            )).toList();

        return new SubjectCriteriaResponse(
            adjInfo.getBaseDate(),
            adjInfo.getExceptionStartDate(),
            adjInfo.getExceptionEndDate(),
            gradeDtos,
            paymentDtos
        );
    }

    @Transactional
    public List<RankIncrementRate> saveRankIncrementRates(Long adjInfoId, RankIncrementRateRequest request) {

        AdjInfo existingAdjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        AdjInfo updatedAdjInfo = existingAdjInfo.toBuilder()
            .evalAnnualSalaryIncrement(request.getEvalDiffIncrementPromoted())
            .evalPerformProvideRate(request.getEvalDiffBonusPromoted())
            .build();

        // 모든 rank_id -> grade_id -> 상세 정보 매핑
        List<RankIncrementRate> rankIncrementRates = request.getRankData().entrySet().stream()
            .flatMap(rankEntry -> {
                Long rankId = rankEntry.getKey();
                Rank rank = rankRepository.findById(rankId)
                    .orElseThrow(() -> new IllegalArgumentException("Rank not found with ID: " + rankId));

                return rankEntry.getValue().entrySet().stream().map(gradeEntry -> {
                    Long gradeId = gradeEntry.getKey();
                    Grade grade = gradeRepository.findById(gradeId)
                        .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));

                    return RankIncrementRate.builder()
                        .adjInfo(updatedAdjInfo)
                        .rank(rank)
                        .grade(grade)
                        .evalDiffBonus(gradeEntry.getValue().getEvalDiffBonus())
                        .evalDiffIncrement(gradeEntry.getValue().getEvalDiffIncrement())
                        .build();
                });
            })
            .collect(Collectors.toList());

        return rankIncrementRateRepository.saveAll(rankIncrementRates);
    }

    @Transactional
    public List<RankIncrementRate> updateRankIncrementRates(Long adjInfoId, RankIncrementRateRequest request) {
        AdjInfo existingAdjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        AdjInfo updatedAdjInfo = existingAdjInfo.toBuilder()
            .evalAnnualSalaryIncrement(request.getEvalDiffIncrementPromoted())
            .evalPerformProvideRate(request.getEvalDiffBonusPromoted())
            .build();

        List<RankIncrementRate> updatedRankIncrementRates = request.getRankData().entrySet().stream()
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
                    Optional<RankIncrementRate> existingRecord =
                        rankIncrementRateRepository.findByRankIdAndAdjInfoIdAndGradeId(rankId, adjInfoId, gradeId);

                    if (existingRecord.isPresent()) {
                        // 기존 데이터 업데이트
                        RankIncrementRate existingRate = existingRecord.get();
                        existingRate = existingRate.toBuilder()
                            .evalDiffBonus(detail.getEvalDiffBonus())
                            .evalDiffIncrement(detail.getEvalDiffIncrement())
                            .build();
                        return existingRate;
                    } else {
                        // 새로운 데이터 생성
                        return RankIncrementRate.builder()
                            .adjInfo(updatedAdjInfo)
                            .rank(rank)
                            .grade(grade)
                            .evalDiffBonus(detail.getEvalDiffBonus())
                            .evalDiffIncrement(detail.getEvalDiffIncrement())
                            .build();
                    }
                });
            })
            .collect(Collectors.toList());

        return rankIncrementRateRepository.saveAll(updatedRankIncrementRates);
    }

    @Transactional
    public List<PaybandCriteria> savePaybandCriteria(Long adjInfoId, PaybandCriteriaRequest request) {
        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new IllegalArgumentException("AdjInfo not found with ID: " + adjInfoId));

        List<PaybandCriteria> paybandCriteriaList = request.getGradeData().entrySet().stream()
            .map(entry -> {
                Long gradeId = entry.getKey();
                Grade grade = gradeRepository.findById(gradeId)
                    .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));

                PaybandCriteriaRequest.PaybandCriteriaDetail detail = entry.getValue();

                return PaybandCriteria.builder()
                    .adjInfo(adjInfo)
                    .grade(grade)
                    .upperLimitPrice(detail.getUpperLimitPrice())
                    .lowerLimitPrice(detail.getLowerLimitPrice())
                    .build();
            })
            .collect(Collectors.toList());

        return paybandCriteriaRepository.saveAll(paybandCriteriaList);
    }

    @Transactional
    public List<PaybandCriteria> updatePaybandCriteria(Long adjInfoId, PaybandCriteriaRequest request) {
        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        List<PaybandCriteria> updatedPaybandCriteriaList = request.getGradeData().entrySet().stream()
            .map(entry -> {
                Long gradeId = entry.getKey();
                Grade grade = gradeRepository.findById(gradeId)
                    .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));

                PaybandCriteriaRequest.PaybandCriteriaDetail detail = entry.getValue();

                Optional<PaybandCriteria> existingRecord =
                    paybandCriteriaRepository.findByAdjInfo_IdAndGrade_Id(adjInfoId, gradeId);

                if (existingRecord.isPresent()) {
                    PaybandCriteria existingCriteria = existingRecord.get();
                    existingCriteria = existingCriteria.toBuilder()
                        .upperLimitPrice(detail.getUpperLimitPrice())
                        .lowerLimitPrice(detail.getLowerLimitPrice())
                        .build();
                    return existingCriteria;
                } else {
                    return PaybandCriteria.builder()
                        .adjInfo(adjInfo)
                        .grade(grade)
                        .upperLimitPrice(detail.getUpperLimitPrice())
                        .lowerLimitPrice(detail.getLowerLimitPrice())
                        .build();
                }
            })
            .collect(Collectors.toList());

        return paybandCriteriaRepository.saveAll(updatedPaybandCriteriaList);
    }

    @Transactional
    public void deletePaybandCriteria(PaybandCriteriaDeleteRequest request) {
        paybandCriteriaRepository.deleteByIdIn(request.getPaybandIds());
    }
}
