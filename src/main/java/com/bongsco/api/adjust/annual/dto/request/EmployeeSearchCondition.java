package com.bongsco.api.adjust.annual.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EmployeeSearchCondition {

    private String name;
    private List<String> grade;
    private LocalDate hireDateFrom;
    private LocalDate hireDateTo;
    private String sortKey;
    private String sortOrder;

    public String getSortField() {
        return switch (sortKey) {
            case "성명" -> "name";
            case "직번" -> "empNum";
            case "채용일자" -> "hireDate";
            case "평가등급" -> "rankName";
            default -> "hireDate";
        };
    }

    public Sort.Direction getSortDirection() {
        return "내림차순".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    public Sort getSort() {
        return Sort.by(getSortDirection(), getSortField());
    }
}

