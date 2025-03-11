package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjInfoUpdateRequest {
    private List<AdjInfoUpdateDto> changed_adj_infos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdjInfoUpdateDto {
        private Long id;
        private Integer year;
        private Integer month;
        private String adj_type;
        private String remarks;
    }
}
