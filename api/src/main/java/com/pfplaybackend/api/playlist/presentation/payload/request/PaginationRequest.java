package com.pfplaybackend.api.playlist.presentation.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class PaginationRequest {
    @NotNull(message = "Page cannot be null")
    private final int pageNo;
    @NotNull(message = "Page Size cannot be null")
    private final int pageSize;

    PaginationRequest(@BindParam("pageNo") int pageNo,
                      @BindParam("pageSize") int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}

