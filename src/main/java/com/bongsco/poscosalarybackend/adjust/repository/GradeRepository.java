package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.user.domain.Grade;
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByIdIn(List<Long> gradeIds);
}
