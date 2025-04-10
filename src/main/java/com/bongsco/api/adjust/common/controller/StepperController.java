package com.bongsco.api.adjust.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.common.dto.response.StepperResponse;
import com.bongsco.api.adjust.common.service.AdjustStepService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stepper API", description = "Stepper 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stepper/{adjustId}")
public class StepperController {
    private final AdjustStepService adjustStepService;

    @Operation(summary = "step 불러오기", description = "stepper 정보 불러오기")
    @GetMapping
    public ResponseEntity<StepperResponse> getStepperData(@PathVariable("adjustId") Long adjustId
    ) {
        return ResponseEntity.ok(adjustStepService.getSteps(adjustId));
    }

    @Operation(summary = "step 상태 바꾸기", description = "저장시 완료로 바꿀때 씀")
    @PatchMapping("/step/{stepId}")
    public ResponseEntity<Void> changeState(@PathVariable("adjustId") Long adjustId,
        @PathVariable("stepId") String stepId,
        @RequestParam(required = false, defaultValue = "true") Boolean isDone
    ) {
        adjustStepService.changeIsDone(adjustId, stepId, isDone);
        return ResponseEntity.noContent().build();
    }
}
