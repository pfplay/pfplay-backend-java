package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class SearchMusicListRequest {
    @NotNull(message = "q cannot be null")
    private final String q;

    private final String platform;

    SearchMusicListRequest(@BindParam("q") String q, @BindParam("platform") String platform) {
        this.q = q;
        this.platform = platform;
    }
}

