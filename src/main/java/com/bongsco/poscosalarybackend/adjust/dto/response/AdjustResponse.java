package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.util.List;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustResponse {
    private final String message;
    private final AdjInfoData data;

    @Data
    @Builder
    public static class AdjInfoData {
        private List<AdjInfo> adj_info;
    }
}
