package com.bongsco.mobile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaybandAppliedType {
    UPPER("적용(상한)"), LOWER("적용(하한)"), NONE("미적용");

    private final String displayName;
}
