package com.pfplaybackend.api.playlist.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class PaginationRequest {
    @NotNull(message = "Page cannot be null")
    private final int page;
    @NotNull(message = "Page Size cannot be null")
    private final int pageSize;

    PaginationRequest(@BindParam("page") int page,
                      @BindParam("pageSize") int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}

