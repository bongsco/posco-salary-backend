package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.entity.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, String> {
    List<Step> findAllByOrderByOrderNumberAsc();
}
