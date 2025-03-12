package com.bongsco.poscosalarybackend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.Salary;
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    public List<Salary> findByAdjInfo_Id(Long id);
}
