package com.bongsco.api.adjust.common.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.PersistenceCreator;

import com.bongsco.api.adjust.common.domain.AdjustType;
import com.bongsco.api.adjust.common.domain.StepName;
import com.bongsco.api.adjust.common.repository.reflection.AdjustItemProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustResponse {
    List<AdjustItemDto> adjustItems;
    private Long totalPage;

    @Builder(toBuilder = true)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class AdjustItemDto implements AdjustItemProjection {
        private Long id;
        private Integer year;
        private Integer month;
        private String adjustType;

        private Integer orderNumber;

        private String stepName;
        private String detailStepName;

        private Boolean isSubmitted;
        private LocalDate baseDate;
        private LocalDate startDate;
        private LocalDate endDate;
        private String author;

        @PersistenceCreator
        public AdjustItemDto(
            Long id,
            Integer year,
            Integer month,
            String adjustType,
            Integer orderNumber,
            String stepName,
            String detailStepName,
            Boolean isSubmitted,
            LocalDate baseDate,
            LocalDate startDate,
            LocalDate endDate,
            String author) {
            this.id = id;
            this.year = year;
            this.month = month;
            this.adjustType = AdjustType.valueOf(adjustType).getDisplayName();
            this.orderNumber = orderNumber;

            if (stepName != null) {
                this.stepName = StepName.valueOf(stepName).getDisplayName();
                this.detailStepName = detailStepName;
            } else {
                this.stepName = null;
                this.detailStepName = null;
            }

            this.isSubmitted = isSubmitted;
            this.baseDate = baseDate;
            this.startDate = startDate;
            this.endDate = endDate;
            this.author = author;
        }
    }
}
