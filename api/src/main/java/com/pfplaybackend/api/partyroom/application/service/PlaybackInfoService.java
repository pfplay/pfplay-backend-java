package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackHistoryDto;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.partyroom.application.proxy.UserProfileProxyService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaybackInfoService {

    private final PartyroomRepository partyroomRepository;
    private final PlaybackRepository playbackRepository;
    private final PlaybackConverter playbackConverter;
    private final MusicQueryPeerService musicQueryService;
    private final UserProfilePeerService userProfileService;

    @Transactional
    public Playback getNextPlaybackInPlaylist(PartyroomId partyroomId, Dj dj) {
        MusicDto musicDto = musicQueryService.getFirstMusic(dj.getPlaylistId());
        return Playback.create(partyroomId , dj.getUserId(), musicDto);
    }

    @Transactional
    public void updatePlaybackAggregation(Playback playback, List<Integer> deltaRecord) {
        Playback updatedPlayback = playback.updateAggregation(deltaRecord.get(0), deltaRecord.get(1), deltaRecord.get(2));
        playbackRepository.save(playbackConverter.toData(updatedPlayback));
    }

    @Transactional
    public Playback getPlaybackById(PlaybackId playbackId) {
        PlaybackData playbackData = playbackRepository.findById(playbackId.getId()).orElseThrow();
        return playbackConverter.toDomain(playbackData);
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
