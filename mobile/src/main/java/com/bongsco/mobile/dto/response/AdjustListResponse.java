package com.bongsco.mobile.dto.response;

import java.util.List;

import com.bongsco.mobile.domain.AdjustType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustListResponse {
    List<AdjustInfoResponse> adjustList;
    private Integer totalPages;
    private Integer pageNumber;

    public static AdjustListResponse from(List<AdjustInfoResponse> adjustList, Integer totalPages, Integer pageNumber){
        return new AdjustListResponse(adjustList, totalPages, pageNumber);
    }
}
