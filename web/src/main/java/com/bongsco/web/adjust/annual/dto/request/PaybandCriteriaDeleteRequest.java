package com.bongsco.web.adjust.annual.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaybandCriteriaDeleteRequest {
    @NotEmpty(message = "요청된 삭제가 없습니다.")
    private List<Long> paybandIds;
}

