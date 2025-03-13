package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoDeleteRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoPostRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoUpdateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;
import com.bongsco.poscosalarybackend.global.domain.AdjType;
import com.bongsco.poscosalarybackend.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustService {
    private final AdjustRepository adjustRepository;

    public AdjustResponse getAdjustInfo(Long startYear, Long endYear) {
        List<AdjInfo> adjInfoList;

        if (startYear != null && endYear != null) {
            adjInfoList = adjustRepository.findByYearBetween(startYear, endYear);
        } else {
            adjInfoList = adjustRepository.findAll();
        }

        return AdjustResponse.builder()
            .message("Successfully brought information")
            .data(AdjustResponse.AdjInfoData.builder().adjInfo(adjInfoList).build())
            .build();
    }

    @Transactional
    public void updateAdjustInfo(Long id, AdjInfoUpdateRequest request) {
        AdjInfo adjInfo = adjustRepository.findById(id)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        AdjInfoUpdateRequest.AdjInfoUpdateDto dto = request.getChangedAdjInfos().get(0);

        AdjInfo updatedAdjInfo = adjInfo.toBuilder()
            .year(dto.getYear())
            .month(dto.getMonth())
            .adjType(AdjType.valueOf(dto.getAdjType()))
            .remarks(dto.getRemarks())
            .build();
        adjustRepository.save(updatedAdjInfo);
    }

    @Transactional
    public void deleteAdjustInfo(AdjInfoDeleteRequest request) {
        for (Long id : request.getDeleted_ids()) {
            AdjInfo adjInfo = adjustRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
            adjustRepository.delete(adjInfo);
        }
    }

    @Transactional
    public void postAdjustInfo(AdjInfoPostRequest postRequest) {
        List<AdjInfo> adjInfoList = postRequest.getAddedAdjInfos().stream()
            .map(dto -> AdjInfo.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .adjType(AdjType.valueOf(dto.getAdjType()))
                .remarks(dto.getRemarks())
                .creationTimestamp(dto.getCreationTimestamp())
                .creator(dto.getCreator())
                .build())
            .collect(Collectors.toList());

        adjustRepository.saveAll(adjInfoList);
    }
}
