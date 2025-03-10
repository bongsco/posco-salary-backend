package com.bongsco.poscosalarybackend.user.dto;

import com.bongsco.poscosalarybackend.user.domain.Department;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDto {
    private long id;
    private String departmentName;
    private String departmentCode;

    public DepartmentDto(long id, String departmentName, String departmentCode) {
        this.id = id;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
    }

    public static DepartmentDto from(Department department) {
        return new DepartmentDto(department.getId(), department.getDepName(), department.getDepCode());
    }
}
