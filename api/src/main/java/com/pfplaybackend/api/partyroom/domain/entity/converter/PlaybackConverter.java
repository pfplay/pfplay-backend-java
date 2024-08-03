package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackConverter {
    public Playback toDomain(PlaybackData playbackData) {
        return Playback.builder()
                .id(playbackData.getId())
                .partyroomId(playbackData.getPartyroomId())
                .userId(playbackData.getUserId())
                .linkId(playbackData.getLinkId())
                .name(playbackData.getName())
                .duration(playbackData.getDuration())
                .thumbnailImage(playbackData.getThumbnailImage())
                .likeCount(playbackData.getLikeCount())
                .dislikeCount(playbackData.getDislikeCount())
                .grabCount(playbackData.getGrabCount())
                .endTime(playbackData.getEndTime())
                .build();
    }

    public PlaybackData toData(Playback playback) {
        return PlaybackData.builder()
                .id(playback.getId())
                .partyroomId(playback.getPartyroomId())
                .userId(playback.getUserId())
                .linkId(playback.getLinkId())
                .name(playback.getName())
                .duration(playback.getDuration())
                .thumbnailImage(playback.getThumbnailImage())
                .likeCount(playback.getLikeCount())
                .dislikeCount(playback.getDislikeCount())
                .grabCount(playback.getGrabCount())
                .endTime(playback.getEndTime())
                .build();
    }
}
