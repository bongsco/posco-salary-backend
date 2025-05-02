package com.bongsco.web.adjust.common.repository.reflection;

import com.bongsco.web.adjust.common.entity.AdjustSubject;
import com.bongsco.web.employee.entity.Employee;
public interface EmployeeAndSalaryProjection {
    Employee getEmployee();
    AdjustSubject getAdjustSubject();
    Double getUpperBoundMemo();
    Double getLowerBoundMemo();
}
