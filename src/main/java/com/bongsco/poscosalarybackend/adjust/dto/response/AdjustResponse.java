package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.util.List;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;

import lombok.Data;

@Data
public class AdjustResponse {
    private String message;
    private AdjInfoData data;

    @Data
    public static class AdjInfoData {
        private List<AdjInfo> adj_info;
    }
}
