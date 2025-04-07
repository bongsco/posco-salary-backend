package com.bongsco.api.global.domain;

public enum StepName {
    기준설정("기준설정"),
    사전작업("사전작업"),
    본연봉조정("본조정");

    private final String description;

    StepName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
