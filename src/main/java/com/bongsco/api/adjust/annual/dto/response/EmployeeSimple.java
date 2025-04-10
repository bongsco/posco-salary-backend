package com.bongsco.api.adjust.annual.dto.response;

import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EmployeeSimple {

    private Long id;
    private Grade grade;
    private Rank rank;
}
