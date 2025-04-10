package com.bongsco.api.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.employee.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByCode(String code);
}
