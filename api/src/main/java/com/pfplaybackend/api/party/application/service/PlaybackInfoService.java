package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.playback.MusicDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaybackInfoService {

    private final PartyroomRepository partyroomRepository;
    private final PlaybackRepository playbackRepository;
    private final PlaylistQueryPort musicQueryService;
    private final UserProfileQueryPort userProfileService;

    @Transactional
    public PlaybackData getNextPlaybackInPlaylist(PartyroomId partyroomId, DjData dj) {
        MusicDto musicDto = musicQueryService.getFirstMusic(dj.getPlaylistId());
        return PlaybackData.create(partyroomId, dj.getUserId(), musicDto);
    }

    @Transactional
    public void updatePlaybackAggregation(PlaybackData playback, List<Integer> deltaRecord) {
        playback.updateAggregation(deltaRecord.get(0), deltaRecord.get(1), deltaRecord.get(2));
        playbackRepository.save(playback);
    }

    @Transactional
    public PlaybackData getPlaybackById(PlaybackId playbackId) {
        return playbackRepository.findById(playbackId.getId()).orElseThrow();
    }

    @Transactional
    public List<PlaybackHistoryDto> getRecentPlaybackHistory(PartyroomId partyroomId) {
        List<PlaybackData> playbackDataList = partyroomRepository.getRecentPlaybackHistory(partyroomId);
        if(playbackDataList.isEmpty()) {
            return List.of();
        }else {
            List<UserId> userIds =  playbackDataList.stream().map(PlaybackData::getUserId).toList();
            Map<UserId, ProfileSettingDto> dtoMap = userProfileService.getUsersProfileSetting(userIds);
            return playbackDataList.stream().map(playbackData -> {
                ProfileSettingDto dto = dtoMap.get(playbackData.getUserId());
                return new PlaybackHistoryDto(playbackData.getName(), dto.getNickname(),  dto.getAvatarIconUri());
            }).toList();
        }
    }
}
