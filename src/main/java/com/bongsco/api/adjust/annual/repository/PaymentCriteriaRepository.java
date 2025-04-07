package com.bongsco.api.adjust.annual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.annual.domain.PaymentCriteria;
public interface PaymentCriteriaRepository extends JpaRepository<PaymentCriteria, Long> {

    List<PaymentCriteria> findByIdIn(List<Long> paymentCriteriaIds);
}
