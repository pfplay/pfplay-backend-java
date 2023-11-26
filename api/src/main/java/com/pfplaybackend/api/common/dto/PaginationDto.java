package com.pfplaybackend.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "Pagination")
public class PaginationDto {
    @Schema(description = "현재 페이지 번호")
    private int pageNumber;
    @Schema(description = "페이지 크기")
    private int pageSize;
    @Schema(description = "총 페이지 수")
    private int totalPages;
    @Schema(description = "총 항목 수")
    private long totalElements;
    @Schema(description = "다음 존재 여부")
    private boolean hasNext;
}
