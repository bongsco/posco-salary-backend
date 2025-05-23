package com.bongsco.mobile.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bongsco.mobile.dto.response.AdjustDetailResponse;
import com.bongsco.mobile.dto.response.AdjustInfoResponse;
import com.bongsco.mobile.dto.response.AdjustListResponse;
import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.entity.AdjustSubject;
import com.bongsco.mobile.repository.AdjustSubjectRepository;
import com.bongsco.mobile.repository.reflection.AdjustDetailProjection;
import com.bongsco.mobile.repository.reflection.ChartProjection;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class MobileService {
    private final AdjustSubjectRepository adjustSubjectRepository;

    public List<ChartResponse> getChartData(String empNum) {
        List<ChartProjection> projections = adjustSubjectRepository.findFiveRecentChartData(empNum);
        List<ChartResponse> responses = projections.stream().map(projection -> ChartResponse.of(projection)).toList();
        return responses;
    }

    public AdjustListResponse getAdjustList(String empNum, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<AdjustInfoResponse> listAndPageInfo = adjustSubjectRepository.findAdjustInfo(empNum, pageable);
        return AdjustListResponse.from(listAndPageInfo.getContent(), listAndPageInfo.getTotalPages(),
            listAndPageInfo.getNumber() + 1);
    }

    public AdjustDetailResponse getAdjustDetailList(Long adjustId, String empNum) {
        AdjustDetailProjection projection = adjustSubjectRepository.findAdjustDetailProjection(adjustId, empNum);
        AdjustSubject beforeAdjustSubject = adjustSubjectRepository.findBeforeAdjSubject(adjustId, empNum);
        return AdjustDetailResponse.of(projection,
            beforeAdjustSubject == null ? projection.getBeforeStdSalary() : beforeAdjustSubject.getFinalStdSalary());
    }
}
