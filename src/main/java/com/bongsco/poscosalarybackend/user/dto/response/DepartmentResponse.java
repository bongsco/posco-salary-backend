package com.bongsco.poscosalarybackend.user.dto.response;

import com.bongsco.poscosalarybackend.user.domain.Department;
import lombok.Getter;

@Getter
public class DepartmentResponse {
    private final long id;
    private final String depName;
    private final String depCode;

    private DepartmentResponse(Long id, String depName, String depCode) {
        this.id = id;
        this.depName = depName;
        this.depCode = depCode;
    }

    public DepartmentResponse of(Long id, String depName, String depCode) {
        return new DepartmentResponse(id, depName, depCode);
    }

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getDepName(), department.getDepCode());
    }
}


// DTO 는 readonly => getter 랑 required ~~ 만 사용 , setter는 지양하자

// request때 dto를 받을 때는 validation을 빡빡하게 하기