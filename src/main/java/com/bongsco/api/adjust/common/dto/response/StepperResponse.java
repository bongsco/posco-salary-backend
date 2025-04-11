package com.bongsco.api.adjust.common.dto.response;

import static com.bongsco.api.adjust.common.domain.StepName.*;

import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;

import com.bongsco.api.adjust.common.domain.StepName;
import com.bongsco.api.adjust.common.entity.AdjustStep;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StepperResponse {
    Map<StepName, List<StepData>> steps;

    public static StepperResponse from(List<AdjustStep> adjustSteps) {
        List<StepData> criteriaDatas = adjustSteps.stream()
            .filter(adjustStep -> adjustStep.getStep().getName() == CRITERIA)
            .map(StepData::from).toList();

        List<StepData> preparationDatas = adjustSteps.stream()
            .filter(adjustStep -> adjustStep.getStep().getName() == PREPARATION)
            .map(StepData::from).toList();

        List<StepData> mainDatas = adjustSteps.stream()
            .filter(adjustStep -> adjustStep.getStep().getName() == MAIN)
            .map(StepData::from).toList();

        return new StepperResponse(Map.of(CRITERIA, criteriaDatas, PREPARATION, preparationDatas, MAIN, mainDatas));
    }

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class StepData {
        private Long id;
        private String text;
        private String state;
        private String date;
        private String url;

        public static StepData from(AdjustStep adjustStep) {
            return StepData.builder()
                .id(adjustStep.getId())
                .text(adjustStep.getStep().getDetailStepName())
                .date(Boolean.TRUE.equals(adjustStep.getIsDone())
                    ? adjustStep.getUpdatedAt()
                    .format(new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd hh:mm").toFormatter())
                    : null
                )
                .url(adjustStep.getStep().getUrl())
                .state(Boolean.TRUE.equals(adjustStep.getIsDone()) ? "DONE" : "UNDONE").
                build();
        }
    }
}
