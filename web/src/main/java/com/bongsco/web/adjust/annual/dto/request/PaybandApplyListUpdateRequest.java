package com.bongsco.web.adjust.annual.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaybandApplyListUpdateRequest {
    @Valid
    private List<PaybandApplyUpdateRequest> updatedSubjects;
}
