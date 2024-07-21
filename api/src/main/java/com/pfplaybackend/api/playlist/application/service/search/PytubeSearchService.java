package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchMusicResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PytubeSearchService implements YoutubeSearchService {

    @Value("${service-api.pytube.uri}")
    private String BASE_URI;
    @Value("${service-api.pytube.api-key}")
    private String API_KEY;
    @Value("${service-api.pytube.api-secret}")
    private String API_SECRET;

    private final RestTemplate restTemplate;

    @Override
    public SearchMusicResultDto searchByWord(String query, int rows) {
        String prefix = "/api/v1/video/search";
        String url = BASE_URI + prefix;

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .queryParam("rows", rows)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("API_KEY", API_KEY);
        headers.set("API_SECRET", API_SECRET);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<SearchMusicResultDto> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, SearchMusicResultDto.class);

        return response.getBody();
    }
}
