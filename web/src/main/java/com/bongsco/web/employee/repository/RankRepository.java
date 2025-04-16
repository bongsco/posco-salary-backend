package com.bongsco.web.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.web.employee.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByCode(String code);
}
