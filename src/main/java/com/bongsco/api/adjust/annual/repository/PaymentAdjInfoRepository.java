package com.bongsco.api.adjust.annual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.annual.domain.PaymentAdjInfo;
public interface PaymentAdjInfoRepository extends JpaRepository<PaymentAdjInfo, Long> {
    void deleteByAdjInfoId(Long adjInfoId);

    List<PaymentAdjInfo> findByAdjInfoId(Long adjInfoId);
}
