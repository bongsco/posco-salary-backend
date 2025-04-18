package com.bongsco.web.adjust.annual.dto.response;

import com.bongsco.web.employee.entity.Grade;
import com.bongsco.web.employee.entity.Rank;

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
