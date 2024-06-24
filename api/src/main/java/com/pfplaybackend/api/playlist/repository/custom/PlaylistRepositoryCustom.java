package com.pfplaybackend.api.playlist.repository.custom;

import com.pfplaybackend.api.playlist.application.dto.PlaylistDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.Tuple;

import java.util.List;

public interface PlaylistRepositoryCustom {
    public List<PlaylistSummary> findAllByUserId(UserId userId);
    public PlaylistSummary findByIdAndUserId(Long playlistId, UserId userId);
    public List<Long> findByUserIdAndListIdAndType(UserId userId, List<Long> listIds, PlaylistType type);
    public Long deleteByListIds(List<Long> listIds);
}