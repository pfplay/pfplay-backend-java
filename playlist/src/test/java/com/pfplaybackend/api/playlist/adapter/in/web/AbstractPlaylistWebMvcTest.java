package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import com.pfplaybackend.api.playlist.application.service.TrackCommandService;
import com.pfplaybackend.api.playlist.application.service.TrackQueryService;
import com.pfplaybackend.api.playlist.adapter.in.web.search.MusicSearchController;
import com.pfplaybackend.api.playlist.application.service.search.MusicSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
        PlaylistCommandController.class,
        PlaylistQueryController.class,
        TrackCommandController.class,
        TrackQueryController.class,
        MusicSearchController.class
})
@Import(AbstractPlaylistWebMvcTest.SharedMethodSecurityConfig.class)
public abstract class AbstractPlaylistWebMvcTest {

    @EnableMethodSecurity
    static class SharedMethodSecurityConfig {}

    @Autowired protected MockMvc mockMvc;
    @MockBean protected PlaylistCommandService playlistCommandService;
    @MockBean protected PlaylistQueryService playlistQueryService;
    @MockBean protected TrackCommandService trackCommandService;
    @MockBean protected TrackQueryService trackQueryService;
    @MockBean protected MusicSearchService musicSearchService;
    @MockBean protected JwtDecoder jwtDecoder;
}
