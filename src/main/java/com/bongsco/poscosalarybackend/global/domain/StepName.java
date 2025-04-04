package com.bongsco.poscosalarybackend.global.domain;

public enum StepName {
    CRITERIA("기준설정"),
    PREPARATION("사전작업"),
    MAIN("본조정");

    private final String description;

    StepName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
