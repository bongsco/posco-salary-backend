package com.bongsco.api.adjust.annual.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRateUpdateRequest {
    @Schema(example = "0.5")
    private Double hpoSalaryIncrementRate;
    @Schema(example = "600")
    private Double hpoExtraBonusMultiplier;

    // P1, P2, P3...
    @Schema(
        description = "직급별 평가등급별 인상률 및 보너스",
        example = """
            {
              "P1": {
                "S": { "incrementRate": 5.0, "bonusMultiplier": 400 },
                "A": { "incrementRate": 4.5, "bonusMultiplier": 350 }
              },
              "P2": {
                "S": { "incrementRate": 4.8, "bonusMultiplier": 390 },
                "A": { "incrementRate": 4.2, "bonusMultiplier": 320 }
              }
            }
            """
    )
    private Map<String, Map<String, PaymentRateValue>> rank_rate;

    @Getter
    @Setter
    @Schema(description = "직급 및 평가 등급별 인상률 및 보너스 정보")
    public static class PaymentRateValue {
        @Schema(example = "4.5")
        private Double incrementRate;
        @Schema(example = "350")
        private Double bonusMultiplier;
    }
}
