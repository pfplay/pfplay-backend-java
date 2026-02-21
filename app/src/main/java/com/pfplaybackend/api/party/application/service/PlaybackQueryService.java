package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackAggregationRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaybackQueryService {

    private final PartyroomQueryPort partyroomQueryPort;
    private final PlaybackRepository playbackRepository;
    private final PlaybackAggregationRepository playbackAggregationRepository;
    private final UserProfileQueryPort userProfileQueryPort;

    @Transactional(readOnly = true)
    public PlaybackData getPlaybackById(PlaybackId playbackId) {
        return playbackRepository.findById(playbackId.getId()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public PlaybackAggregationData getPlaybackAggregationById(PlaybackId playbackId) {
        return playbackAggregationRepository.findById(playbackId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<PlaybackHistoryDto> getRecentPlaybackHistory(PartyroomId partyroomId) {
        List<PlaybackData> playbackDataList = partyroomQueryPort.getRecentPlaybackHistory(partyroomId);
        if(playbackDataList.isEmpty()) {
            return List.of();
        }else {
            List<UserId> userIds =  playbackDataList.stream().map(PlaybackData::getUserId).toList();
            Map<UserId, ProfileSettingDto> dtoMap = userProfileQueryPort.getUsersProfileSetting(userIds);
            return playbackDataList.stream().map(playbackData -> {
                ProfileSettingDto dto = dtoMap.get(playbackData.getUserId());
                return new PlaybackHistoryDto(playbackData.getName(), dto.nickname(),  dto.avatarIconUri());
            }).toList();
        }
    }
}
