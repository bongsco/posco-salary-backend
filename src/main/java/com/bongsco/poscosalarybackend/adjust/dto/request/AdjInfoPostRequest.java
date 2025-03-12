package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class AdjInfoPostRequest {
    private List<AdjInfoDto> added_adj_infos;

    @Data
    public static class AdjInfoDto {

        private Integer year;
        private Integer month;
        private String adj_type;
        private String remarks;
        private LocalDate creationTimestamp;
        private String creator;

        private BigDecimal evalAnnualSalaryIncrement = BigDecimal.valueOf(0.0);
        private BigDecimal evalPerformProvideRate = BigDecimal.valueOf(400.00);

        private Integer orderNumber = 1;

        public void setEvalAnnualSalaryIncrement(BigDecimal evalAnnualSalaryIncrement) {
            this.evalAnnualSalaryIncrement =
                (evalAnnualSalaryIncrement == null) ? BigDecimal.valueOf(0.00) : evalAnnualSalaryIncrement;
        }

        public void setEvalPerformProvideRate(BigDecimal evalPerformProvideRate) {
            this.evalPerformProvideRate =
                (evalPerformProvideRate == null) ? BigDecimal.valueOf(400.00) : evalPerformProvideRate;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = (orderNumber == null) ? 1 : orderNumber;
        }
    }
}
