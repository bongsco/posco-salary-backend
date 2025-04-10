package com.bongsco.api.adjust.annual.dto.request;

import com.bongsco.api.adjust.common.entity.PaybandAppliedType;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaybandApplyUpdateRequest {
    @NotNull(message = "아이디는 null 일 수 없습니다.")
    private Long adjustSubjectId;

    @NotNull(message = "payband 적용 여부는 null 일 수 없습니다.")
    private PaybandAppliedType isPaybandApplied;
}
