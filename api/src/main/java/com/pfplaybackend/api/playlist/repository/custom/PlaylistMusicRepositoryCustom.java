package com.pfplaybackend.api.playlist.repository.custom;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistMusicRepositoryCustom {
    public Page<PlaylistMusicDto> getMusicsWithPagination(Long playlistId, Pageable pageable);
    public Long deleteByPlayListIds(List<Long> listIds);
    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId);
}