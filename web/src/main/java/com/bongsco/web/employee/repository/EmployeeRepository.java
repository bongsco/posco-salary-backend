package com.bongsco.web.employee.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.web.adjust.annual.dto.response.EmployeeSimple;
import com.bongsco.web.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT new com.bongsco.web.adjust.annual.dto.response.EmployeeSimple(e.id, e.grade, e.rank)
            FROM Employee e
            WHERE e.grade.id IN :gradeIds AND e.employmentType.id IN :employmentTypeIds
        """)
    List<EmployeeSimple> findSimpleEmployeesByCriteria(@Param("gradeIds") Set<Long> gradeIds,
        @Param("employmentTypeIds") Set<Long> employmentTypeIds);
}
