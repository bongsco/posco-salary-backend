package com.bongsco.api.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.employee.entity.EmploymentType;

public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {
}
