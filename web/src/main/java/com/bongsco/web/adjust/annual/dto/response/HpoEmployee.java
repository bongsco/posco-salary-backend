package com.bongsco.web.adjust.annual.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HpoEmployee {
    private Long employeeId;
    private String empNum;
    private String name;
    private String depName;
    private String gradeName;
    private String rankName;
    private Boolean isInHpo;
}