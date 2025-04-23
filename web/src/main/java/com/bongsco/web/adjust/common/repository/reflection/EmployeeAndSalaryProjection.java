package com.bongsco.web.adjust.common.repository.reflection;

import com.bongsco.web.employee.entity.Employee;
public interface EmployeeAndSalaryProjection {
    Employee getEmployee();
    Double getFinalStdSalary();
}
