package com.bongsco.poscosalarybackend.adjust.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.GradeAdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.PaymentAdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.PaymentCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.dto.request.PaybandCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.RankIncrementRateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.SubjectCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.adjust.repository.GradeAdjInfoRepository;
import com.bongsco.poscosalarybackend.adjust.repository.GradeRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaybandCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaymentAdjInfoRepository;
import com.bongsco.poscosalarybackend.adjust.repository.PaymentCriteriaRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankIncrementRateRepository;
import com.bongsco.poscosalarybackend.adjust.repository.RankRepository;
import com.bongsco.poscosalarybackend.global.exception.CustomException;
import com.bongsco.poscosalarybackend.global.exception.ErrorCode;
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

    @Transactional
    public SubjectCriteriaRequest addSubjectInfo(Long adjInfoId, SubjectCriteriaRequest request) {
        AdjInfo existingAdjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 기존 데이터 업데이트 (toBuilder 사용)
        AdjInfo updatedAdjInfo = existingAdjInfo.toBuilder()
            .baseDate(request.getBaseDate() != null ? request.getBaseDate() : existingAdjInfo.getBaseDate())
            .exceptionStartDate(request.getExceptionStartDate() != null ? request.getExceptionStartDate() :
                existingAdjInfo.getExceptionStartDate())
            .exceptionEndDate(request.getExceptionEndDate() != null ? request.getExceptionEndDate() :
                existingAdjInfo.getExceptionEndDate())
            .build();

        adjustRepository.save(updatedAdjInfo);

        // 기존 GradeAdjInfo 및 PaymentAdjInfo 삭제 후 갱신
        if (request.getGradeIds() != null) {
            gradeAdjInfoRepository.deleteAllByAdjInfo(existingAdjInfo);
            List<Grade> grades = gradeRepository.findByIdIn(request.getGradeIds());
            List<GradeAdjInfo> gradeAdjInfos = grades.stream()
                .map(grade -> new GradeAdjInfo(null, grade, updatedAdjInfo))
                .collect(Collectors.toList());
            gradeAdjInfoRepository.saveAll(gradeAdjInfos);
        }

        if (request.getPaymentCriteriaIds() != null) {
            paymentAdjInfoRepository.deleteAllByAdjInfo(existingAdjInfo);
            List<PaymentCriteria> paymentCriteria = paymentCriteriaRepository.findByIdIn(
                request.getPaymentCriteriaIds());
            List<PaymentAdjInfo> paymentAdjInfos = paymentCriteria.stream()
                .map(payment -> new PaymentAdjInfo(null, payment, updatedAdjInfo))
                .collect(Collectors.toList());
            paymentAdjInfoRepository.saveAll(paymentAdjInfos);
        }

        return request;
    }

    @Transactional
    public List<RankIncrementRate> saveRankIncrementRates(Long adjInfoId, RankIncrementRateRequest request) {

        AdjInfo existingAdjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

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
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

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
    public PaybandCriteriaConfigListResponse getPaybandCriteria(Long adjInfoId) {

        List<PaybandCriteria> existingPaybandCriteriaList = paybandCriteriaRepository.findByAdjInfo_Id(adjInfoId);
        Set<Long> existingGradeIdSet = existingPaybandCriteriaList.stream()
            .map(pc -> pc.getGrade().getId())
            .collect(Collectors.toSet());

        List<Grade> grades = gradeRepository.findAll();
        List<Grade> leftGrades = grades.stream()
            .filter(grade -> !existingGradeIdSet.contains(grade.getId()))
            .collect(Collectors.toList());

        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid adjInfoId: " + adjInfoId));

        List<PaybandCriteria> newPaybandCriteriaList = leftGrades.stream()
            .map(grade ->
                PaybandCriteria.builder()
                    .grade(grade)
                    .adjInfo(adjInfo)
                    .upperLimitPrice(130.0)
                    .lowerLimitPrice(70.0)
                    .build()
            ).collect(Collectors.toList());

        paybandCriteriaRepository.saveAll(newPaybandCriteriaList);

        existingPaybandCriteriaList.addAll(newPaybandCriteriaList);

        return PaybandCriteriaConfigListResponse.from(existingPaybandCriteriaList);
    }

    @Transactional
    public List<PaybandCriteria> updatePaybandCriteria(Long adjInfoId, PaybandCriteriaRequest request) {
        AdjInfo adjInfo = adjustRepository.findById(adjInfoId)
            .orElseThrow(() -> new IllegalArgumentException("AdjInfo not found with ID: " + adjInfoId));

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
}
