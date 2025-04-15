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

    private List<Integer> year;
    private List<Integer> month;
    private List<AdjustType> adjType;
    private List<Boolean> state;
    private List<Boolean> isSubmitted;
    private List<String> author;

    private List<Map<String, String>> sorts;
}
