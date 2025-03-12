package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdjInfoPostRequest {
    private final List<AdjInfoDto> added_adj_infos;

    // ************************************* To-DO-List *************************************
    // evalAnnualSalaryIncrement, evalPerformProvideRate, orderNumber JPA Not Null 로 바꿔야합니다.
    // ***************************************************************************************

    @Getter
    @NoArgsConstructor
    public static class AdjInfoDto {
        private final Integer year;
        private final Integer month;
        private final String adjType;
        private final String remarks;
        private final LocalDate creationTimestamp;
        private final String creator;

        public AdjInfoDto(Integer year, Integer month, String adjType, String remarks,
            LocalDate creationTimestamp, String creator,
            BigDecimal evalAnnualSalaryIncrement, BigDecimal evalPerformProvideRate,
            Integer orderNumber) {
            this.year = year;
            this.month = month;
            this.adjType = adjType;
            this.remarks = remarks;
            this.creationTimestamp = creationTimestamp;
            this.creator = creator;
            this.evalAnnualSalaryIncrement =
                (evalAnnualSalaryIncrement == null) ? BigDecimal.valueOf(0.00) : evalAnnualSalaryIncrement;
            this.evalPerformProvideRate =
                (evalPerformProvideRate == null) ? BigDecimal.valueOf(400.00) : evalPerformProvideRate;
            this.orderNumber = (orderNumber == null) ? 1 : orderNumber;
        }
    }
}
