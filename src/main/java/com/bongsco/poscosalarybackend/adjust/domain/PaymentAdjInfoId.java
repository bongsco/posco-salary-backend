package com.bongsco.poscosalarybackend.adjust.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentAdjInfoId implements Serializable {

    private Long paymentId;
    private Long adjInfoId;

    public PaymentAdjInfoId() {
    }

    public PaymentAdjInfoId(Long paymentId, Long adjInfoId) {
        this.paymentId = paymentId;
        this.adjInfoId = adjInfoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentAdjInfoId that = (PaymentAdjInfoId)o;
        return Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(adjInfoId, that.adjInfoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, adjInfoId);
    }
}

