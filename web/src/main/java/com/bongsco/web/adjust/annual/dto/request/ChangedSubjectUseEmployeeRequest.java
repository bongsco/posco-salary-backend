package com.bongsco.web.adjust.annual.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChangedSubjectUseEmployeeRequest {
    @Valid
    private List<ChangedSubjectUseEmployee> changedSubjectUseEmployee;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedSubjectUseEmployee {
        @NotNull(message = "id가 null 일 수 없습니다.")
        private Long employeeId;
        @NotNull(message = "대상자 여부가 null 일 수 없습니다.")
        private Boolean subjectUse;
    }
}
