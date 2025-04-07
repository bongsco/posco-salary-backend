package com.bongsco.api.adjust.common.entity;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.api.adjust.common.domain.StepName;
import com.bongsco.api.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE step SET deleted = true WHERE id = ?")
public class Step extends BaseEntity {
    @Id
    private String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StepName stepName;

    @Column(length = 20, nullable = false)
    private String detailStepName;

    @Column(nullable = false)
    private Integer orderNumber;

    @Column(length = 100, nullable = false)
    private String url;
}
