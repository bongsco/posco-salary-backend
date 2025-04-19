package com.bongsco.mobile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StepName {
    CRITERIA("기준설정"),
    PREPARATION("사전작업"),
    MAIN("본연봉조정");

    private final String displayName;
}
