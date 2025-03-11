package com.bongsco.poscosalarybackend.global.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@SQLDelete(sql = "UPDATE {h-schema} SET deleted = true WHERE id = ?")  // Soft Delete 적용
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate  // 생성 시 자동으로 설정됨
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate  // 업데이트 시 자동으로 설정됨
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column  // 삭제 시각 (Soft Delete)
    private boolean deleted = false;
}

