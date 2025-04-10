package com.bongsco.api.adjust.common.dto.request;

import java.util.List;
import java.util.Map;

import com.bongsco.api.adjust.common.domain.AdjustType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustSearchRequest {
    private Integer page;
    private Integer size;

    private Integer year;
    private Integer month;
    private AdjustType adjType;
    private Boolean state;
    private Boolean isSubmitted;
    private String author;

    private List<Map<String, String>> sorts;
}
