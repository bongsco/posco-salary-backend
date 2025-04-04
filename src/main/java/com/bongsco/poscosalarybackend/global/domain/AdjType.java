package com.bongsco.poscosalarybackend.global.domain;

public enum AdjType {
    BASEUP("Baseup"),
    승진자연봉조정("승진자연봉조정"),
    정기연봉조정("정기연봉조정");

    private final String description;

    AdjType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

