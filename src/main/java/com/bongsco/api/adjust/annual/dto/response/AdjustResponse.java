package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;

import com.bongsco.api.adjust.annual.domain.AdjInfo;

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
