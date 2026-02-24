package com.pfplaybackend.api.playlist.adapter.out.persistence.custom;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummaryDto;

import java.util.List;

public interface PlaylistRepositoryCustom {
    List<PlaylistSummaryDto> findAllByUserId(UserId userId);

    PlaylistSummaryDto findByIdAndUserId(Long playlistId, UserId userId);

    Long deleteByListIds(List<Long> listIds);
}