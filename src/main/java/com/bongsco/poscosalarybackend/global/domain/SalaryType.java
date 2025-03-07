package com.bongsco.poscosalarybackend.global.domain;


public enum SalaryType {
    BASEUP("Baseup"),
    PROMOTION("승진자 연봉 조정"),
    ANNUAL_SALARY_ADJUSTMENT("정기연봉조정");

    private final String description;

    SalaryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

