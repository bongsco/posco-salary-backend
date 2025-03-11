package com.bongsco.poscosalarybackend.adjust.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse.AdjInfoData;
import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;

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
}
