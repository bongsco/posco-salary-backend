package com.bongsco.poscosalarybackend.adjust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.PaymentAdjInfo;
public interface PaymentAdjInfoRepository extends JpaRepository<PaymentAdjInfo, Long> {
    void deleteAllByAdjInfo(AdjInfo adjInfo);
}
