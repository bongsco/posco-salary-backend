package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_adj_info")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payment_adj_info SET deleted = 1 WHERE payment_id = ? AND adj_info_id = ?")
public class PaymentAdjInfo extends BaseEntity {

    @EmbeddedId
    private PaymentAdjInfoId id;

    @ManyToOne
    @MapsId("paymentId")
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentCriteria paymentCriteria;

    @ManyToOne
    @MapsId("adjInfoId")
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
