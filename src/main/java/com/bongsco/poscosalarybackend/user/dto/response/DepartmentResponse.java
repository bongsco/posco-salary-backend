package com.bongsco.poscosalarybackend.user.dto.response;

import com.bongsco.poscosalarybackend.user.domain.Department;

import lombok.Getter;

@Getter
public class DepartmentResponse {
    private final Long id;
    private final String depName;
    private final String depCode;

    private DepartmentResponse(Long id, String depName, String depCode) {
        this.id = id;
        this.depName = depName;
        this.depCode = depCode;
    }

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getDepName(), department.getDepCode());
    }

    public DepartmentResponse of(Long id, String depName, String depCode) {
        return new DepartmentResponse(id, depName, depCode);
    }
}
