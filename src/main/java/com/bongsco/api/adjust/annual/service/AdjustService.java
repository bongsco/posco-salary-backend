package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.global.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.repository.AdjustRepository;
import com.bongsco.api.adjust.annual.domain.AdjInfo;
import com.bongsco.api.adjust.annual.dto.request.AdjustPostRequest;
import com.bongsco.api.adjust.annual.dto.request.AdjustUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjustResponse;
import com.bongsco.api.global.domain.AdjustType;
import com.bongsco.api.global.exception.CustomException;

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

    public void updateAdjust(Long id, AdjustUpdateRequest request) {
        AdjInfo adjust = adjustRepository.findById(id)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        adjust.toBuilder().interfaceUse(request.getIsSubmitted());

        adjustRepository.save(adjust);
    }

    public void deleteAdjustInfo(Long adjustId) {
        adjustRepository.deleteById(adjustId);
    }

    @Transactional
    public void postAdjustInfo(AdjustPostRequest postRequest) {
        List<AdjInfo> adjInfoList = postRequest.getAddedAdjInfos().stream()
            .map(dto -> AdjInfo.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .adjustType(AdjustType.valueOf(dto.getAdjType()))
                .remarks(dto.getRemarks())
                .creationTimestamp(dto.getCreationTimestamp())
                .creator(dto.getCreator())
                .build())
            .collect(Collectors.toList());

        adjustRepository.saveAll(adjInfoList);
    }

    public Long getBeforeAdjInfoId(Long adjInfoId) {
        List<AdjInfo> adjInfos = adjustRepository.findLatestAdjustInfo(adjInfoId)
            .stream()
            .filter(adjInfo1 -> !adjInfo1.getDeleted())
            .toList();
        if (adjInfos.isEmpty()) {
            throw new CustomException(CANNOT_NULL_INPUT);
        }
        Long beforeAdjInfoId = adjInfos.get(0).getId();
        return beforeAdjInfoId;
    }
}
