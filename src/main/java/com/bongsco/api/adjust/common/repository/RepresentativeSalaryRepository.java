package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.entity.RepresentativeSalary;
@Repository
public interface RepresentativeSalaryRepository extends JpaRepository<RepresentativeSalary, Long> {
    List<RepresentativeSalary> findByAdjustId(Long id);
}
