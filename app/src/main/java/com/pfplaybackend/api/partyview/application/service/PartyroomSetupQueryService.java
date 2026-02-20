package com.pfplaybackend.api.partyview.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.AggregationDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.partyview.adapter.in.web.payload.response.QueryPartyroomSetupResponse;
import com.pfplaybackend.api.partyview.application.dto.CrewSetupDto;
import com.pfplaybackend.api.partyview.application.dto.CurrentDjDto;
import com.pfplaybackend.api.partyview.application.dto.DisplayDto;
import com.pfplaybackend.api.partyview.application.dto.ReactionDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomSetupQueryService {

    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final PlaybackRepository playbackRepository;
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    private final UserProfileQueryPort userProfileQueryPort;

    @Transactional(readOnly = true)
    public QueryPartyroomSetupResponse getSetupInfo(PartyroomId partyroomId) {
        List<CrewSetupDto> crews = getCrewsForSetup(partyroomId);
        DisplayDto display = getDisplayInfo();
        return QueryPartyroomSetupResponse.from(crews, display);
    }

    private List<CrewSetupDto> getCrewsForSetup(PartyroomId partyroomId) {
        List<CrewData> crews = crewRepository.findByPartyroomDataIdAndIsActiveTrue(partyroomId.getId());
        List<UserId> userIds = crews.stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileQueryPort.getUsersProfileSetting(userIds);

        return crews.stream().map(crew -> {
            UserId userId = crew.getUserId();
            return CrewSetupDto.from(crew, profileSettingMap.get(userId));
        }).toList();
    }

    private DisplayDto getDisplayInfo() {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ActivePartyroomDto activePartyroom = partyroomRepository.getActivePartyroomByUserId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));
        boolean isPlaybackActivated = activePartyroom.isPlaybackActivated();

        if (isPlaybackActivated) {
            PlaybackData playback = playbackRepository.findById(activePartyroom.currentPlaybackId().getId()).orElseThrow();
            CrewId currentDjCrewId = activePartyroom.currentDjCrewId();
            CurrentDjDto currentDjDto = new CurrentDjDto(currentDjCrewId.getId());

            Optional<PlaybackReactionHistoryData> optional = playbackReactionHistoryRepository.findByPlaybackIdAndUserId(
                    new PlaybackId(playback.getId()), authContext.getUserId());
            AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
            ReactionDto reactionDto = ReactionDto.from(getHistory(optional), aggregationDto);
            PlaybackDto playbackDto = PlaybackDto.withEndTime(playback.getId(), playback.getLinkId(), playback.getName(), playback.getDuration().toDisplayString(), playback.getThumbnailImage(), playback.getEndTime());
            return new DisplayDto(true, playbackDto, reactionDto, currentDjDto);
        } else {
            return new DisplayDto(false, null, null, null);
        }
    }

    private static Map<String, Boolean> getHistory(Optional<PlaybackReactionHistoryData> optional) {
        return optional.map(data -> Map.of(
                "isLiked", data.isLiked(),
                "isDislike", data.isDisliked(),
                "isGrabbed", data.isGrabbed()
        )).orElseGet(() -> Map.of(
                "isLiked", false,
                "isDislike", false,
                "isGrabbed", false
        ));
    }
}
