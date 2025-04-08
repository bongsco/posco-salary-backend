package com.bongsco.api.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByGradeIdInAndEmploymentTypeIdIn(List<Long> gradeIds, List<Long> employmentTypeIds);
}
