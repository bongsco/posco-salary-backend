package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangedSubjectUseEmployeeRequest {
    private List<ChangedSubjectUseEmployee> changedSubjectUseEmployee;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedSubjectUseEmployee {
        private Long employeeId;
        private Boolean subjectUse;
    }
}
