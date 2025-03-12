package com.bongsco.poscosalarybackend.adjust.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChangedSubjectRequest {
    private final Long adjSubjectId;
    private final Boolean paybandUse;

    public ChangedSubjectRequest(Long adjSubjectId, Boolean paybandUse) {
        this.adjSubjectId = adjSubjectId;
        this.paybandUse = paybandUse;
    }
}
