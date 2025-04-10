package com.bongsco.api.adjust.common.dto.response;

import static com.bongsco.api.adjust.common.domain.StepName.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            .map(StepData::from).collect(Collectors.toList());

        List<StepData> preparationDatas = adjustSteps.stream()
            .filter(adjustStep -> adjustStep.getStep().getName() == PREPARATION)
            .map(StepData::from).collect(Collectors.toList());

        List<StepData> mainDatas = adjustSteps.stream()
            .filter(adjustStep -> adjustStep.getStep().getName() == MAIN)
            .map(StepData::from).collect(Collectors.toList());

        return new StepperResponse(Map.of(CRITERIA, criteriaDatas, PREPARATION, preparationDatas, MAIN, mainDatas));
    }

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class StepData {
        private Map<String, Object> step;

        public static StepData from(AdjustStep adjustStep) {
            Map<String, Object> step = new HashMap<>();
            step.put("id", adjustStep.getId());
            step.put("text", adjustStep.getStep().getDetailStepName());
            if (adjustStep.getIsDone()) {
                step.put("state", "DONE");
                step.put("date", adjustStep.getUpdatedAt());
            } else {
                step.put("state", "UNDONE");
            }
            step.put("url", adjustStep.getStep().getUrl());
            return new StepData(step);
        }
    }
}
