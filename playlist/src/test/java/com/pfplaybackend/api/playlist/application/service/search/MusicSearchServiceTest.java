package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import com.pfplaybackend.api.playlist.application.dto.search.SearchResultRawDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicSearchServiceTest {

    @Mock
    YoutubeSearchService youtubeSearchService;

    @InjectMocks
    MusicSearchService musicSearchService;

    @Test
    @DisplayName("getSearchList — 검색 결과를 정상 반환한다")
    void getSearchListReturnsResults() {
        // given
        SearchResultDto result = new SearchResultDto("success", List.of(
                new SearchResultRawDto("vid1", "Title 1", "url1", "3:30", "thumb1"),
                new SearchResultRawDto("vid2", "Title 2", "url2", "4:00", "thumb2")
        ));
        when(youtubeSearchService.searchByWord("test", 10)).thenReturn(result);

        // when
        SearchResultDto actual = musicSearchService.getSearchList("test");

        // then
        assertThat(actual.data()).hasSize(2);
        assertThat(actual.data().get(0).video_id()).isEqualTo("vid1");
        assertThat(actual.data().get(1).video_id()).isEqualTo("vid2");
    }

    @Test
    @DisplayName("getSearchList — 빈 검색 결과를 반환한다")
    void getSearchListReturnsEmpty() {
        // given
        SearchResultDto result = new SearchResultDto("success", List.of());
        when(youtubeSearchService.searchByWord("empty", 10)).thenReturn(result);

        // when
        SearchResultDto actual = musicSearchService.getSearchList("empty");

        // then
        assertThat(actual.data()).isEmpty();
    }
}
