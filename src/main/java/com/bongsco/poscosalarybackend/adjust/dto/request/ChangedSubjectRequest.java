package com.bongsco.poscosalarybackend.adjust.dto.request;

import lombok.Getter;

@Getter
public class ChangedSubjectRequest {
    private final Long adjSubjectId;
    private final Boolean paybandUse;

    private ChangedSubjectRequest(Long adjSubjectId, Boolean paybandUse) {
        this.adjSubjectId = adjSubjectId;
        this.paybandUse = paybandUse;
    }

    public static ChangedSubjectRequest of(Long adjSubjectId, Boolean paybandUse) {
        return new ChangedSubjectRequest(adjSubjectId, paybandUse);ß
    }
}
