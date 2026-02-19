package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.service.DjDomainService;
import com.pfplaybackend.api.party.domain.service.PlaybackDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.infrastructure.repository.PlaybackRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybackManagementServiceDjQueueChangeTest {

    @Mock private PlaybackRepository playbackRepository;
    @Mock private PlaybackConverter playbackConverter;
    @Mock private PlaybackDomainService playbackDomainService;
    @Mock private DjDomainService djDomainService;
    @Mock private PlaybackInfoService playbackInfoService;
    @Mock private PartyroomInfoService partyroomInfoService;
    @Mock private UserActivityPeerService userActivityService;
    @Mock private RedisMessagePublisher messagePublisher;
    @Mock private PartyroomRepository partyroomRepository;
    @Mock private PartyroomConverter partyroomConverter;
    @Mock private ExpirationTaskScheduler scheduleService;
    @Mock private CrewDomainService crewDomainService;

    @InjectMocks
    private PlaybackManagementService playbackManagementService;

    private final PartyroomId partyroomId = new PartyroomId(1L);

    private Dj createDj(long id, UserId userId, long crewId, int orderNumber) {
        return Dj.builder()
                .id(id)
                .partyroomId(partyroomId)
                .userId(userId)
                .crewId(new CrewId(crewId))
                .playlistId(new PlaylistId(10L))
                .orderNumber(orderNumber)
                .isQueued(true)
                .build();
    }

    private Crew createCrew(long id, UserId userId) {
        return Crew.builder()
                .id(id)
                .partyroomId(partyroomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("start - 플레이백 시작 시 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void start_shouldPublishDjQueueChangeEvent() {
        // given
        UserId djUserId = new UserId();
        Dj dj1 = createDj(100L, djUserId, 1L, 1);
        Crew crew1 = createCrew(1L, djUserId);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);
        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew1);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .playbackTimeLimit(0)
                .build();
        partyroom.assignDjSet(djSet);
        partyroom.assignCrewSet(crewSet);

        // rotateDjQueue -> save -> toDomain 결과
        PartyroomData savedPartyroomData = mock(PartyroomData.class);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(savedPartyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(savedPartyroomData);
        when(partyroomConverter.toDomain(savedPartyroomData)).thenReturn(partyroom);

        // getNextPlaybackInPlaylist 결과
        Playback nextPlayback = Playback.builder()
                .partyroomId(partyroomId)
                .userId(djUserId)
                .name("Test Song")
                .duration("3:30")
                .linkId("abc123")
                .thumbnailImage("thumb.jpg")
                .endTime(System.currentTimeMillis() + 210000)
                .build();
        when(playbackInfoService.getNextPlaybackInPlaylist(eq(partyroomId), any(Dj.class))).thenReturn(nextPlayback);

        // playbackRepository.save 결과
        PlaybackData playbackData = PlaybackData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .userId(djUserId)
                .name("Test Song")
                .duration("3:30")
                .linkId("abc123")
                .thumbnailImage("thumb.jpg")
                .endTime(System.currentTimeMillis() + 210000)
                .build();
        when(playbackRepository.save(any(PlaybackData.class))).thenReturn(playbackData);
        when(playbackConverter.toData(any(Playback.class))).thenReturn(playbackData);

        // scheduleTask
        lenient().when(playbackDomainService.convertToSeconds("3:30")).thenReturn(210L);

        // getDjs 결과
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(List.of(
                new DjWithProfileDto(1L, 1, "dj_nick", "icon.png")
        ));

        // when
        playbackManagementService.start(partyroom);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PLAYBACK_START), any());
    }

    @Test
    @DisplayName("start - DJ가 여러 명일 때 회전 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void start_multipleDjs_shouldPublishDjQueueChangeEvent() {
        // given
        UserId djUserId1 = new UserId();
        UserId djUserId2 = new UserId();
        UserId djUserId3 = new UserId();

        Dj dj1 = createDj(100L, djUserId1, 1L, 1);
        Dj dj2 = createDj(101L, djUserId2, 2L, 2);
        Dj dj3 = createDj(102L, djUserId3, 3L, 3);
        Crew crew1 = createCrew(1L, djUserId1);
        Crew crew2 = createCrew(2L, djUserId2);
        Crew crew3 = createCrew(3L, djUserId3);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);
        djSet.add(dj2);
        djSet.add(dj3);
        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew1);
        crewSet.add(crew2);
        crewSet.add(crew3);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .playbackTimeLimit(0)
                .build();
        partyroom.assignDjSet(djSet);
        partyroom.assignCrewSet(crewSet);

        PartyroomData savedPartyroomData = mock(PartyroomData.class);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(savedPartyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(savedPartyroomData);
        when(partyroomConverter.toDomain(savedPartyroomData)).thenReturn(partyroom);

        Playback nextPlayback = Playback.builder()
                .partyroomId(partyroomId)
                .userId(djUserId1)
                .name("Song")
                .duration("4:00")
                .linkId("xyz")
                .thumbnailImage("thumb.jpg")
                .endTime(System.currentTimeMillis() + 240000)
                .build();
        when(playbackInfoService.getNextPlaybackInPlaylist(eq(partyroomId), any(Dj.class))).thenReturn(nextPlayback);

        PlaybackData playbackData = PlaybackData.builder()
                .id(2L)
                .partyroomId(partyroomId)
                .userId(djUserId1)
                .name("Song")
                .duration("4:00")
                .linkId("xyz")
                .thumbnailImage("thumb.jpg")
                .endTime(System.currentTimeMillis() + 240000)
                .build();
        when(playbackRepository.save(any(PlaybackData.class))).thenReturn(playbackData);
        when(playbackConverter.toData(any(Playback.class))).thenReturn(playbackData);

        lenient().when(playbackDomainService.convertToSeconds("4:00")).thenReturn(240L);

        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(List.of(
                new DjWithProfileDto(2L, 1, "dj2", "icon2.png"),
                new DjWithProfileDto(3L, 2, "dj3", "icon3.png"),
                new DjWithProfileDto(1L, 3, "dj1", "icon1.png")
        ));

        // when
        playbackManagementService.start(partyroom);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PLAYBACK_START), any());
    }

    @Test
    @DisplayName("tryProceed - DJ가 없으면 PARTYROOM_DEACTIVATION만 발행하고 DJ_QUEUE_CHANGE는 발행하지 않는다")
    void tryProceed_noDj_shouldNotPublishDjQueueChangeEvent() {
        // given
        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();
        partyroom.assignDjSet(new HashSet<>());
        partyroom.assignCrewSet(new HashSet<>());

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);
        PartyroomData savedData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(djDomainService.isExistDj(partyroom)).thenReturn(false);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(savedData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(savedData);

        // when — complete() 호출 → tryProceed() 실행
        playbackManagementService.complete(partyroomId, new UserId());

        // then
        verify(messagePublisher).publish(eq(MessageTopic.PARTYROOM_DEACTIVATION), any());
        verify(messagePublisher, never()).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
    }
}
