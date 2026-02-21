package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.playlist.application.dto.PlaybackTrackDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
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
    private final PlaylistCommandPort playlistCommandPort;
    private final UserProfileQueryPort userProfileQueryPort;

    @Transactional
    public PlaybackData getNextPlaybackInPlaylist(PartyroomId partyroomId, DjData dj, UserId djUserId) {
        PlaybackTrackDto trackDto = playlistCommandPort.getFirstTrack(dj.getPlaylistId());
        return PlaybackData.create(partyroomId, djUserId,
                trackDto.name(), trackDto.duration(), trackDto.linkId(), trackDto.thumbnailImage());
    }

    @Transactional
    public PlaybackAggregationData updatePlaybackAggregation(Long playbackId, List<Integer> deltaRecord) {
        PlaybackAggregationData aggregation = playbackAggregationRepository.findById(playbackId).orElseThrow();
        aggregation.updateAggregation(deltaRecord.get(0), deltaRecord.get(1), deltaRecord.get(2));
        return playbackAggregationRepository.save(aggregation);
    }

    @Transactional(readOnly = true)
    public PlaybackData getPlaybackById(PlaybackId playbackId) {
        return playbackRepository.findById(playbackId.getId()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public PlaybackAggregationData getPlaybackAggregationById(Long playbackId) {
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
