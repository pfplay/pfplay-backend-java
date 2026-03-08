package com.pfplaybackend.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "공통 성공 응답")
@Getter
public class ApiCommonResponse<T> {
    @Schema(description = "응답 데이터")
    private final T data;

    @Builder
    public ApiCommonResponse(T data) {
        this.data = data;
    }

    public static <T> ApiCommonResponse<T> success(T data) {
        return ApiCommonResponse.<T>builder()
                .data(data)
                .build();
    }

    public static ApiCommonResponse<Void> ok() {
        return new ApiCommonResponse<>(null);
    }
}
