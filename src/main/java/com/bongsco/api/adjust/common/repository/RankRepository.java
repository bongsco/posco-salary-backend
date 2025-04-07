package com.bongsco.api.adjust.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.employee.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Long> {
}
