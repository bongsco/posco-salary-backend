package com.bongsco.web.adjust.annual.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRateDto {
    private Double incrementRate;
    private Double bonusMultiplier;
}
