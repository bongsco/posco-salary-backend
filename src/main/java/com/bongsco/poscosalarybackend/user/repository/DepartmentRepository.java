package com.bongsco.poscosalarybackend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.user.domain.Department;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAll();
}
