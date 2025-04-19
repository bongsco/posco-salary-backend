package com.bongsco.mobile.dto.response;

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
public class AdjustInfoResponse {
    private Long id;
     private Integer year;
     private Integer orderNumber;
     private AdjustType adjustType;
}
