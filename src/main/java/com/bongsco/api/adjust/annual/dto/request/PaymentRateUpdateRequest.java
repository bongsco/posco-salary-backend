package com.bongsco.api.adjust.annual.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRateUpdateRequest {
    @Schema(example = "0.5")
    @PositiveOrZero
    @NotNull(message = "HPO 인상률은 null일 수 없습니다.")
    private Double hpoSalaryIncrementRate;

    @Schema(example = "600")
    @PositiveOrZero
    @NotNull(message = "HPO 추가 보너스는 null일 수 없습니다.")
    private Double hpoExtraBonusMultiplier;

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
    @Valid
    @NotNull(message = "직급별 인상률/보너스 정보는 필수입니다.")
    private Map<String, Map<String, PaymentRateValue>> paymentRates;

    @Getter
    @Setter
    @Schema(description = "직급 및 평가 등급별 인상률 및 보너스 정보")
    public static class PaymentRateValue {
        @Schema(example = "4.5")
        @PositiveOrZero
        @NotNull
        private Double incrementRate;

        @Schema(example = "350")
        @PositiveOrZero
        @NotNull
        private Double bonusMultiplier;
    }
}


