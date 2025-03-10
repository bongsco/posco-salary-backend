package com.bongsco.poscosalarybackend.user.repository;

import com.bongsco.poscosalarybackend.user.domain.Department;
import com.bongsco.poscosalarybackend.user.service.DepartmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    public List<Department> findAll();
}
