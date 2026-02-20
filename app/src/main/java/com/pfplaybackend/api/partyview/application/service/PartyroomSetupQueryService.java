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

import java.util.HashMap;
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
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        Optional<ActivePartyroomDto> optActivePartyroom = partyroomRepository.getActivePartyroomByUserId(authContext.getUserId());
        if (optActivePartyroom.isEmpty()) throw ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM);

        ActivePartyroomDto activePartyroom = optActivePartyroom.get();
        PartyroomId partyroomId = new PartyroomId(activePartyroom.id());
        boolean isPlaybackActivated = activePartyroom.isPlaybackActivated();

        if (isPlaybackActivated) {
            PlaybackData playback = playbackRepository.findById(activePartyroom.currentPlaybackId().getId()).orElseThrow();
            CrewData djInfo = getCurrentDjInfo(partyroomId, playback);
            CurrentDjDto currentDjDto = new CurrentDjDto(djInfo.getId());

            Optional<PlaybackReactionHistoryData> optional = playbackReactionHistoryRepository.findByPlaybackIdAndUserId(
                    new PlaybackId(playback.getId()), authContext.getUserId());
            AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
            ReactionDto reactionDto = ReactionDto.from(getHistory(optional), aggregationDto);
            PlaybackDto playbackDto = new PlaybackDto(playback.getId(), playback.getLinkId(), playback.getName(), playback.getDuration().toDisplayString(), playback.getThumbnailImage(), playback.getEndTime());
            return new DisplayDto(true, playbackDto, reactionDto, currentDjDto);
        } else {
            return new DisplayDto(false, null, null, null);
        }
    }

    private CrewData getCurrentDjInfo(PartyroomId partyroomId, PlaybackData playback) {
        return crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), playback.getUserId()).orElseThrow();
    }

    private static Map<String, Boolean> getHistory(Optional<PlaybackReactionHistoryData> optional) {
        Map<String, Boolean> history = new HashMap<>();
        if (optional.isPresent()) {
            PlaybackReactionHistoryData playbackReactionHistoryData = optional.get();
            history.put("isLiked", playbackReactionHistoryData.isLiked());
            history.put("isDislike", playbackReactionHistoryData.isDisliked());
            history.put("isGrabbed", playbackReactionHistoryData.isGrabbed());
        } else {
            history.put("isLiked", false);
            history.put("isDislike", false);
            history.put("isGrabbed", false);
        }
        return history;
    }
}
