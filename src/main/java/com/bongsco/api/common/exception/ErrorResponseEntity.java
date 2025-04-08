package com.bongsco.api.common.exception;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponseEntity.builder()
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build()
            );
    }
}
