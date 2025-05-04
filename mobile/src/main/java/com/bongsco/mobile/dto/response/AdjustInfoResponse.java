package com.bongsco.mobile.dto.response;

import com.bongsco.mobile.domain.AdjustType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustInfoResponse {
    private Long id;
    private Integer year;
    private Integer orderNumber;
    private String adjustTypeName;

    public AdjustInfoResponse(Long id, Integer year, Integer orderNumber, AdjustType adjustType) {
        this.id = id;
        this.year = year;
        this.orderNumber = orderNumber;
        this.adjustTypeName = adjustType.getDisplayName();
    }
}
