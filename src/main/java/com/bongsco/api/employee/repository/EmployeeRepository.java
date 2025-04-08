package com.bongsco.api.employee.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT e FROM Employee e
            JOIN FETCH e.grade
            JOIN FETCH e.employmentType
            WHERE e.grade.id IN :gradeIds AND e.employmentType.id IN :employmentTypeIds
        """)
    List<Employee> findWithJoinByGradeIdsAndEmploymentTypeIds(@Param("gradeIds") Set<Long> gradeIds,
        @Param("employmentTypeIds") Set<Long> employmentTypeIds);
}
