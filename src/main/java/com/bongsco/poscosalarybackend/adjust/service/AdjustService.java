package com.bongsco.poscosalarybackend.adjust.service;

import static com.bongsco.poscosalarybackend.global.exception.ErrorCode.*;

import java.util.List;

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

        AdjustResponse adjustResponse = new AdjustResponse();
        adjustResponse.setMessage("Successfully brought information");

        AdjustResponse.AdjInfoData adjInfoData = new AdjustResponse.AdjInfoData();
        adjInfoData.setAdj_info(adjInfoList);

        adjustResponse.setData(adjInfoData);

        return adjustResponse;
    }

    @Transactional
    public void updateAdjustInfo(Long id, AdjInfoUpdateRequest request) {
        AdjInfo adjInfo = adjustRepository.findById(id)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        AdjInfoUpdateRequest.AdjInfoUpdateDto dto = request.getChanged_adj_infos().get(0);

        adjInfo.setYear(dto.getYear());
        adjInfo.setMonth(dto.getMonth());
        adjInfo.setAdjType(AdjType.valueOf(dto.getAdj_type()));
        adjInfo.setRemarks(dto.getRemarks());
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
        for (AdjInfoPostRequest.AdjInfoDto dto : postRequest.getAdded_adj_infos()) {
            AdjInfo adjInfo = new AdjInfo();

            adjInfo.setYear(dto.getYear());
            adjInfo.setMonth(dto.getMonth());
            adjInfo.setAdjType(AdjType.valueOf(dto.getAdjType()));
            adjInfo.setRemarks(dto.getRemarks());
            adjInfo.setCreationTimestamp(dto.getCreationTimestamp());
            adjInfo.setCreator(dto.getCreator());
            adjInfo.setEvalAnnualSalaryIncrement(dto.getEvalAnnualSalaryIncrement());
            adjInfo.setEvalPerformProvideRate(dto.getEvalPerformProvideRate());
            adjInfo.setOrderNumber(dto.getOrderNumber());

            adjustRepository.save(adjInfo);
        }
    }
}
