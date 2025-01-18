package com.pfplaybackend.api.playlist.repository.custom;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;

public interface PlaylistRepositoryCustom {
    List<PlaylistSummary> findAllByUserId(UserId userId);
    PlaylistSummary findByIdAndUserId(Long playlistId, UserId userId);
    Long deleteByListIds(List<Long> listIds);
}