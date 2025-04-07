package com.bongsco.api.adjust.annual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.user.domain.Rank;
public interface RankRepository extends JpaRepository<Rank, Long> {
}
