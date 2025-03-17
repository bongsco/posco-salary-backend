package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.adjust.domain.PaymentCriteria;
public interface PaymentCriteriaRepository extends JpaRepository<PaymentCriteria, Long> {

    List<PaymentCriteria> findByIdIn(List<Long> paymentCriteriaIds);
}
