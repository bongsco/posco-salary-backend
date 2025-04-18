package com.bongsco.web.adjust.annual.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRateUpdateResponse {
    private List<String> updatedGrades;
}
