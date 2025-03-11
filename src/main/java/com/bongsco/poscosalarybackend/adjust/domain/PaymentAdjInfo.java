package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_adj_info")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payment_adj_info SET deleted = true WHERE payment_id = ? AND adj_info_id = ?ß")
public class PaymentAdjInfo extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentCriteria paymentCriteria;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
