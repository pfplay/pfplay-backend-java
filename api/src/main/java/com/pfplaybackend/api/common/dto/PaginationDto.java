package com.pfplaybackend.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pagination")
public record PaginationDto(
        @Schema(description = "현재 페이지 번호") int pageNumber,
        @Schema(description = "페이지 크기") int pageSize,
        @Schema(description = "총 페이지 수") int totalPages,
        @Schema(description = "총 항목 수") long totalElements,
        @Schema(description = "다음 존재 여부") boolean hasNext
) {
}
