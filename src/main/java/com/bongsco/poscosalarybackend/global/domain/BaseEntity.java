package com.bongsco.poscosalarybackend.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate  // 생성 시 자동으로 설정됨
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate  // 업데이트 시 자동으로 설정됨
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column  // 삭제 시각 (Soft Delete)
    private boolean deletedAt = Boolean.FALSE;

}

