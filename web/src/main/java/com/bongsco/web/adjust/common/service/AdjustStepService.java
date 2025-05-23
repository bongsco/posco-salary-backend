package com.bongsco.web.adjust.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.web.adjust.common.domain.StepName;
import com.bongsco.web.adjust.common.dto.response.StepperResponse;
import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.adjust.common.entity.AdjustStep;
import com.bongsco.web.adjust.common.entity.Step;
import com.bongsco.web.adjust.common.repository.AdjustRepository;
import com.bongsco.web.adjust.common.repository.AdjustStepRepository;
import com.bongsco.web.adjust.common.repository.StepRepository;
import com.bongsco.web.common.exception.CustomException;
import com.bongsco.web.common.exception.ErrorCode;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustStepService {
    private final AdjustStepRepository adjustStepRepository;
    private final StepRepository stepRepository;
    private final AdjustRepository adjustRepository;
    private final EntityManager entityManager;

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
        adjustStepRepository.resetAdjustStepByAdjustIdAndStepName(adjustId, StepName.MAIN);
        entityManager.clear();
    }
}
