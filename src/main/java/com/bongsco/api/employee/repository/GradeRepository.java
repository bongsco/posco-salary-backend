package com.bongsco.api.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.employee.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
