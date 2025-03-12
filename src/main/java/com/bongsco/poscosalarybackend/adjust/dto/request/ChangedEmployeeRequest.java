package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
