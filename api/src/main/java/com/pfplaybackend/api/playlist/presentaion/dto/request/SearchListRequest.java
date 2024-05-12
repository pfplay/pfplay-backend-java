package com.pfplaybackend.api.playlist.presentaion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class SearchListRequest {
    @NotNull(message = "q cannot be null")
    private final String q;
    private final String pageToken;

    SearchListRequest(@BindParam("q") String q,
                      @BindParam("pageToken") String pageToken) {
        this.q = q;
        this.pageToken = pageToken;
    }
}

