package com.bongsco.mobile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.repository.AdjustSubjectRepository;
import com.bongsco.mobile.repository.reflection.ChartProjection;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class MobileService {
    private final AdjustSubjectRepository adjustSubjectRepository;

    public List<ChartResponse> getChartData(Long employeeId) {
        List<ChartProjection> projections = adjustSubjectRepository.findFiveRecentChartData(employeeId);
        List<ChartResponse> responses = projections.stream().map(projection -> ChartResponse.of(projection)).toList();
        return responses;
    }

}
