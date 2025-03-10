package com.bongsco.poscosalarybackend.global.domain;

public enum Status {
    ING("작업 중"),
    BEFORE("작업 전"),
    AFTER("작업 완료");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
