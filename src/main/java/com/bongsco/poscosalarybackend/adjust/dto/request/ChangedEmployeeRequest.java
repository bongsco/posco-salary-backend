package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

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
public class ChangedEmployeeRequest {
    private List<ChangedEmployee> changedEmployee;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedEmployee {
        private Long employeeId;
        private Boolean subjectUse;
    }
}
