package com.bongsco.mobile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bongsco.mobile.dto.response.BarChartResponse;
import com.bongsco.mobile.repository.AdjustSubjectRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class MobileService {
    private final AdjustSubjectRepository adjustSubjectRepository;

    public List<BarChartResponse> getBarchartData(Long employeeId) {
        List<BarChartResponse> responses = adjustSubjectRepository.findFiveRecentBarchartData(employeeId);
        return responses;
    }
}
