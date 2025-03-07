package com.bongsco.poscosalarybackend.adjust.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "payment_adj_info")
@Data
@SQLDelete(sql = "UPDATE payment_adj_info SET deleted = true WHERE payment_id = ? AND adj_info_id = ?ß")
public class PaymentAdjInfo {
    @Id
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentCriteria paymentCriteria;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
