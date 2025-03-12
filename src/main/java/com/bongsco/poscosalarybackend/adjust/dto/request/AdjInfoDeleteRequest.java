package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class AdjInfoDeleteRequest {
    private List<Long> deleted_ids;
}
