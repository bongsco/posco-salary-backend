package com.bongsco.api.adjust.annual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.domain.RepresentativeSalary;
@Repository
public interface RepresentativeSalaryRepository extends JpaRepository<RepresentativeSalary, Long> {
    List<RepresentativeSalary> findByAdjInfo_Id(Long id);
}
