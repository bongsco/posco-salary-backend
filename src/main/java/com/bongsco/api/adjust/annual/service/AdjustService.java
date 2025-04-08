package com.bongsco.api.adjust.annual.service;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.api.adjust.common.dto.request.AdjustPostRequest;
import com.bongsco.api.adjust.common.dto.request.AdjustUpdateRequest;
import com.bongsco.api.adjust.common.dto.response.AdjustResponse;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustService {
    private final AdjustRepository adjustRepository;

    public AdjustResponse getAdjustInfo(Integer startYear, Integer endYear) {
        List<Adjust> adjustList;

        if (startYear != null && endYear != null) {
            adjustList = adjustRepository.findByYearBetween(startYear, endYear);
        } else {
            adjustList = adjustRepository.findAll();
        }

        return AdjustResponse.builder()
            .message("Successfully brought information")
            .data(AdjustResponse.AdjInfoData.builder().adjust(adjustList).build())
            .build();
    }

    public void updateAdjust(Long id, AdjustUpdateRequest request) {
        Adjust adjust = adjustRepository.findById(id)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        adjust.toBuilder().isSubmitted(request.getIsSubmitted());

        adjustRepository.save(adjust);
    }

    public void deleteAdjustInfo(Long adjustId) {
        adjustRepository.deleteById(adjustId);
    }

    public void postAdjustInfo(AdjustPostRequest postRequest) {
        // TODO("Implement Adjust Post Feature")
    }

    public Long getBeforeAdjInfoId(Long adjInfoId) {
        List<Adjust> adjusts = adjustRepository.findLatestAdjustInfo(adjInfoId)
            .stream()
            .filter(adjInfo1 -> !adjInfo1.getDeleted())
            .toList();
        if (adjusts.isEmpty()) {
            throw new CustomException(CANNOT_NULL_INPUT);
        }
        Long beforeAdjInfoId = adjusts.get(0).getId();
        return beforeAdjInfoId;
    }
}
