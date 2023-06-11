package com.pfplaybackend.api.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private T data;

    @Builder
    public ApiResponse(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }
}
