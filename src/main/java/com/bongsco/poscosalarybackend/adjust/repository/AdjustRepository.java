package com.bongsco.poscosalarybackend.adjust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.user.domain.Department;

@Repository
public interface AdjustRepository extends JpaRepository<Department, Long> {

}
