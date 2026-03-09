package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class MusicSearchRequest {
    @NotBlank(message = "q is required.")
    private final String q;

    private final String platform;

    MusicSearchRequest(@BindParam("q") String q, @BindParam("platform") String platform) {
        this.q = q;
        this.platform = platform;
    }
}
