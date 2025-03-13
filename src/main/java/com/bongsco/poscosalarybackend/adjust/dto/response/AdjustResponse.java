package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.util.List;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustResponse {
    private String message;
    private AdjInfoData data;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class AdjInfoData {
        private List<AdjInfo> adjInfo;
    }
}
