package com.bongsco.web.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "", "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "", "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_NULL_INPUT(BAD_REQUEST, "", "NULL값은 입력할 수 없습니다"),
    EXCEL_DOWNLOAD_MAKE(BAD_REQUEST, "", "엑셀 생성 중 오류 발생"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "", "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "", "현재 내 계정 정보가 존재하지 않습니다"),

    /* 403 FORBIDDEN : 접근 권한 없음 */
    ACCESS_DENIED(FORBIDDEN, "", "접근이 거부되었습니다"),
    INVALID_PERMISSION(FORBIDDEN, "", "유효하지 않은 권한입니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "", "해당 유저 정보를 찾을 수 없습니다"),
    RESOURCE_NOT_FOUND(NOT_FOUND, "", "해당 연봉조정정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "", "로그아웃 된 사용자입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "", "데이터가 이미 존재합니다"),
    ALREADY_COMPLETED(CONFLICT, "", "이미 완료된 단계입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
