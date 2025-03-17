package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjInfoPostRequest {

    @NotEmpty(message = "addedAdjInfos 리스트는 비어있을 수 없습니다.")
    @Valid
    private List<AdjInfoDto> addedAdjInfos;

    // ************************************* To-DO-List *************************************
    // evalAnnualSalaryIncrement, evalPerformProvideRate, orderNumber JPA Not Null 로 바꿔야합니다.
    // ***************************************************************************************

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class AdjInfoDto {

        @NotNull(message = "연도(year)는 필수 입력값입니다.")
        @Min(value = 1900, message = "연도(year)는 1900 이상이어야 합니다.")
        @Max(value = 2100, message = "연도(year)는 2100 이하여야 합니다.")
        private Integer year;

        @NotNull(message = "월(month)은 필수 입력값입니다.")
        @Min(value = 1, message = "월(month)는 1 이상이어야 합니다.")
        @Max(value = 12, message = "월(month)는 12 이하여야 합니다.")
        private Integer month;

        @NotBlank(message = "조정 유형(adjType)은 필수 입력값입니다.")
        @Pattern(regexp = "^(ANNUAL_SALARY_ADJUSTMENT|BASEUP|PROMOTION)$"
            , message = "adjType은 ANNUAL_SALARY_ADJUSTMENT, BASEUP, PROMOTION 중 하나여야 합니다.")
        private String adjType;

        @Size(max = 255, message = "비고(remarks)는 255자 이하로 입력해야 합니다.")
        private String remarks;

        @NotNull(message = "생성일(creationTimestamp)은 필수 입력값입니다.")
        @PastOrPresent(message = "생성일(creationTimestamp)은 현재 날짜 이후일 수 없습니다.")
        private LocalDate creationTimestamp;

        @NotBlank(message = "생성자(creator)는 필수 입력값입니다.")
        @Size(max = 50, message = "creator는 최대 50자까지 입력 가능합니다.")
        private String creator;
    }
}
