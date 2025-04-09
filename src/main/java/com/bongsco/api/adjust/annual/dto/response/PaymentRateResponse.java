// PaymentRateResponse.java
package com.bongsco.api.adjust.annual.dto.response;

import java.util.Map;

import com.bongsco.api.adjust.annual.dto.PaymentRateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentRateResponse {
    private Double hpoSalaryIncrementRate;
    private Double hpoExtraBonusMultiplier;
    private Map<String, Map<String, PaymentRateDto>> paymentRates;
}
