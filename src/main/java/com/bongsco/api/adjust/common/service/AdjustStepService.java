package com.bongsco.api.adjust.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.common.domain.StepName;
import com.bongsco.api.adjust.common.dto.response.StepperResponse;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustStep;
import com.bongsco.api.adjust.common.entity.Step;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.AdjustStepRepository;
import com.bongsco.api.adjust.common.repository.StepRepository;
import com.bongsco.api.common.exception.CustomException;
import com.bongsco.api.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustStepService {
    private final AdjustStepRepository adjustStepRepository;
    private final StepRepository stepRepository;
    private final AdjustRepository adjustRepository;

    public StepperResponse getSteps(Long adjustId) {
        List<AdjustStep> adjustSteps = adjustStepRepository.findByAdjustIdOrderByStep_OrderNumberAsc(adjustId);

        return StepperResponse.from(adjustSteps);
    }

    @Transactional
    public void initializeSteps(Long adjustId) {
        Adjust adjust = adjustRepository.findById(adjustId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        List<Step> steps = stepRepository.findAllByOrderByOrderNumberAsc();
        List<AdjustStep> adjustSteps = steps
            .stream()
            .map(step -> AdjustStep
                .builder()
                .step(step)
                .adjust(adjust)
                .isDone(false)
                .build())
            .toList();

        adjustStepRepository.saveAll(adjustSteps);
    }

    @Transactional
    public void changeIsDone(Long adjustId, String stepId, Boolean isDone) {
        AdjustStep adjustStep = adjustStepRepository.findByAdjust_IdAndStep_Id(adjustId, stepId);
        adjustStepRepository.save(adjustStep.toBuilder().isDone(isDone).build());
    }

    @Transactional
    public void resetMain(Long adjustId) {
        List<AdjustStep> criteriaSteps = adjustStepRepository.findAllByAdjust_IdAndStep_Name(adjustId, StepName.MAIN)
            .stream()
            .map(e -> e.toBuilder().isDone(false).build())
            .toList();

        adjustStepRepository.saveAll(criteriaSteps);
    }
}
