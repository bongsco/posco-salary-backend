package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangedHighPerformGroupEmployeeRequest {
    private List<ChangedHighPerformGroupEmployee> changedHighPerformGroupEmployee;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedHighPerformGroupEmployee {
        private Long employeeId;
        private Boolean inHighPerformGroup;
    }
}
