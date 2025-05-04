package com.bongsco.mobile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AdjustType {
    BASEUP("Base Up"),
    PROMOTED("승진자연봉조정"),
    ANNUAL("정기연봉조정");

    private final String displayName;
}

