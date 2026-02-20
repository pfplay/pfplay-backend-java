package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackTrackDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
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
public class PlaybackInfoService {

    private final PartyroomRepository partyroomRepository;
    private final PlaybackRepository playbackRepository;
    private final PlaylistCommandPort playlistCommandPort;
    private final UserProfileQueryPort userProfileQueryPort;

    @Transactional
    public PlaybackData getNextPlaybackInPlaylist(PartyroomId partyroomId, DjData dj) {
        PlaybackTrackDto trackDto = playlistCommandPort.getFirstTrack(dj.getPlaylistId());
        return PlaybackData.create(partyroomId, dj.getUserId(), trackDto);
    }

    @Transactional
    public void updatePlaybackAggregation(PlaybackData playback, List<Integer> deltaRecord) {
        playback.updateAggregation(deltaRecord.get(0), deltaRecord.get(1), deltaRecord.get(2));
        playbackRepository.save(playback);
    }

    @Transactional(readOnly = true)
    public PlaybackData getPlaybackById(PlaybackId playbackId) {
        return playbackRepository.findById(playbackId.getId()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<PlaybackHistoryDto> getRecentPlaybackHistory(PartyroomId partyroomId) {
        List<PlaybackData> playbackDataList = partyroomRepository.getRecentPlaybackHistory(partyroomId);
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
