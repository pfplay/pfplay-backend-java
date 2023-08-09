package com.pfplaybackend.api.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiCommonResponse<T> {
    private T data;

    @Builder
    public ApiCommonResponse(T data) {
        this.data = data;
    }

    public static <T> ApiCommonResponse<T> success(T data) {
        return ApiCommonResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> ApiCommonResponse<T> error(T data) {
        return ApiCommonResponse.<T>builder()
                .data(data)
                .build();
    }
}
