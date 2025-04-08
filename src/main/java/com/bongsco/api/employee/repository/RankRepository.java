package com.bongsco.api.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.employee.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Long> {
}
