package com.bongsco.poscosalarybackend.adjust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.user.domain.Rank;
public interface RankRepository extends JpaRepository<Rank, Long> {
}
