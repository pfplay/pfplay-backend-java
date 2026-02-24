package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import com.pfplaybackend.api.playlist.application.dto.search.SearchResultRawDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PytubeSearchServiceTest {

    @Mock RestTemplate restTemplate;
    @InjectMocks PytubeSearchService pytubeSearchService;

    void setBaseConfig() {
        ReflectionTestUtils.setField(pytubeSearchService, "BASE_URI", "http://localhost:8080");
        ReflectionTestUtils.setField(pytubeSearchService, "API_KEY", "test-key");
        ReflectionTestUtils.setField(pytubeSearchService, "API_SECRET", "test-secret");
    }

    @Test
    @DisplayName("searchByWord — null/blank/zero duration 항목을 필터링한다")
    void searchByWordFiltersInvalidDuration() {
        // given
        setBaseConfig();
        List<SearchResultRawDto> rawList = List.of(
                new SearchResultRawDto("v1", "Valid Song", "url1", "3:30", "thumb1"),
                new SearchResultRawDto("v2", "Null Duration", "url2", null, "thumb2"),
                new SearchResultRawDto("v3", "Blank Duration", "url3", "", "thumb3"),
                new SearchResultRawDto("v4", "Zero Duration", "url4", "0", "thumb4")
        );
        SearchResultDto mockResult = new SearchResultDto("ok", rawList);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(SearchResultDto.class)))
                .thenReturn(ResponseEntity.ok(mockResult));

        // when
        SearchResultDto result = pytubeSearchService.searchByWord("test", 10);

        // then
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).video_id()).isEqualTo("v1");
    }

    @Test
    @DisplayName("searchByWord — 유효한 결과만 반환한다")
    void searchByWordReturnsOnlyValidResults() {
        // given
        setBaseConfig();
        List<SearchResultRawDto> rawList = List.of(
                new SearchResultRawDto("v1", "Song A", "url1", "4:15", "thumb1"),
                new SearchResultRawDto("v2", "Song B", "url2", "1:02:30", "thumb2")
        );
        SearchResultDto mockResult = new SearchResultDto("ok", rawList);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(SearchResultDto.class)))
                .thenReturn(ResponseEntity.ok(mockResult));

        // when
        SearchResultDto result = pytubeSearchService.searchByWord("music", 5);

        // then
        assertThat(result.data()).hasSize(2);
        assertThat(result.data()).extracting(SearchResultRawDto::video_title)
                .containsExactly("Song A", "Song B");
    }

    @Test
    @DisplayName("searchByWord — 빈 응답이면 빈 리스트를 반환한다")
    void searchByWordReturnsEmptyListWhenNoData() {
        // given
        setBaseConfig();
        SearchResultDto mockResult = new SearchResultDto("ok", List.of());
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(SearchResultDto.class)))
                .thenReturn(ResponseEntity.ok(mockResult));

        // when
        SearchResultDto result = pytubeSearchService.searchByWord("nothing", 10);

        // then
        assertThat(result.data()).isEmpty();
    }
}
