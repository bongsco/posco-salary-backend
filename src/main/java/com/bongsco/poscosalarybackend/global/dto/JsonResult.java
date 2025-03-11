package com.bongsco.poscosalarybackend.global.dto;

import lombok.Getter;

@Getter
public class JsonResult<T> {
    private String message;    // "success" or "fail"
    private String error;            // set if fail or not set
    private T data;                    // set if success or not set

    private JsonResult(T data) {
        message = "success";
        this.data = data;
    }

    private JsonResult(String error) {
        message = "fail";
        this.error = error;
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(data);
    }

    public static JsonResult<String> fail(String message) {
        return new JsonResult<>(message);
    }
}
