package com.pfplaybackend.api.playlist.repository.custom;

import java.util.List;

public interface PlaylistMusicRepositoryCustom {
    public Long deleteByPlayListIds(List<Long> listIds);
    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId);
}