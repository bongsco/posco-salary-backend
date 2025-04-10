package com.bongsco.api.adjust.annual.dto.request;

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
public class ChangedHighPerformGroupEmployeeRequest {
    @Valid
    private List<ChangedHighPerformGroupEmployee> changedHighPerformGroupEmployee;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedHighPerformGroupEmployee {
        @NotNull(message = "직원 아이디는 null 일 수 없습니다.")
        private Long employeeId;
        @NotNull(message = "고성과조직 적용 여부는 null 일 수 없습니다.")
        private Boolean isInHpo;
    }
}
