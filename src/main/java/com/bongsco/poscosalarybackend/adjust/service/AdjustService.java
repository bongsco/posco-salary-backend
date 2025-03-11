package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoUpdateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse.AdjInfoData;
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
            .data(AdjInfoData.builder()
                .adj_info(adjInfoList)
                .build())
            .build();
    }

    @Transactional
    public void updateAdjustInfo(AdjInfoUpdateRequest request) {
        for (AdjInfoUpdateRequest.AdjInfoUpdateDto dto : request.getChanged_adj_infos()) {
            AdjInfo adjInfo = adjustRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            adjInfo.setYear(dto.getYear());
            adjInfo.setMonth(dto.getMonth());
            adjInfo.setAdjType(AdjType.valueOf(dto.getAdj_type()));
            adjInfo.setRemarks(dto.getRemarks());
        }
    }
}
