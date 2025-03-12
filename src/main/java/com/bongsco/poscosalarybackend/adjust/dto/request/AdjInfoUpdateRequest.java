package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdjInfoUpdateRequest {
    private List<AdjInfoUpdateDto> changed_adj_infos;

    @Data
    @NoArgsConstructor
    public static class AdjInfoUpdateDto {
        private Long id;
        private Integer year;
        private Integer month;
        private String adj_type;
        private String remarks;
    }
}
