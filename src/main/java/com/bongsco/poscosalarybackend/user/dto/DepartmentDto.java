package com.bongsco.poscosalarybackend.user.dto;

import com.bongsco.poscosalarybackend.user.domain.Department;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDto {
    private long id;
    private String depName;
    private String depCode;

    public DepartmentDto(Long id, String depName, String depCode) {
        this.id = id;
        this.depName = depName;
        this.depCode = depCode;
    }

    public static DepartmentDto from(Department department) {
        return new DepartmentDto(department.getId(), department.getDepName(), department.getDepCode());
    }
}
