package com.pfplaybackend.api.party.adapter.out.event;

import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.party.application.dto.playback.AggregationDto;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.application.service.PartyroomInfoService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.event.*;
import com.pfplaybackend.api.party.adapter.in.listener.message.*;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventRedisRelay {

    private final RedisMessagePublisher messagePublisher;
    private final UserProfileQueryPort userProfileService;
    private final PartyroomInfoService partyroomInfoService;
    private final CrewRepository crewRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CrewAccessedEvent event) {
        CrewSummaryDto crewSummary;
        if (event.getAccessType() == AccessType.ENTER) {
            CrewData crew = crewRepository.findById(event.getCrewId()).orElseThrow();
            ProfileSettingDto profile = userProfileService.getUserProfileSetting(event.getUserId());
            crewSummary = CrewSummaryDto.from(crew, profile);
        } else {
            crewSummary = CrewSummaryDto.exitOnly(event.getCrewId());
        }
        messagePublisher.publish(MessageTopic.PARTYROOM_ACCESS.topic(),
                PartyroomAccessMessage.create(event.getPartyroomId(), event.getAccessType(), crewSummary));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(DjQueueChangedEvent event) {
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE.topic(),
                DjQueueChangeMessage.create(
                        event.getPartyroomId(),
                        partyroomInfoService.getDjs(event.getPartyroomId().getId())
                ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(PlaybackStartedEvent event) {
        messagePublisher.publish(MessageTopic.PLAYBACK_START.topic(),
                new PlaybackStartMessage(event.getPartyroomId(), MessageTopic.PLAYBACK_START,
                        event.getCrewId(), event.getPlayback()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(PlaybackDeactivatedEvent event) {
        messagePublisher.publish(MessageTopic.PARTYROOM_DEACTIVATION.topic(),
                new PartyroomDeactivationMessage(event.getPartyroomId(), MessageTopic.PARTYROOM_DEACTIVATION));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CrewGradeChangedEvent event) {
        messagePublisher.publish(MessageTopic.CREW_GRADE.topic(),
                CrewGradeMessage.from(event.getPartyroomId(), event.getAdjusterCrewId(),
                        event.getAdjustedCrewId(), event.getPrevGrade(), event.getCurrGrade()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CrewPenalizedEvent event) {
        messagePublisher.publish(MessageTopic.CREW_PENALTY.topic(),
                CrewPenaltyMessage.from(event.getPartyroomId(), event.getPunisherCrewId(),
                        event.getPunishedCrewId(), event.getDetail(), event.getPenaltyType()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(ReactionMotionChangedEvent event) {
        messagePublisher.publish(MessageTopic.REACTION_MOTION.topic(),
                ReactionMotionMessage.from(event.getPartyroomId(), event.getReactionType(),
                        event.getMotionType(), new com.pfplaybackend.api.party.domain.value.CrewId(event.getCrewId())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(ReactionAggregationChangedEvent event) {
        messagePublisher.publish(MessageTopic.REACTION_AGGREGATION.topic(),
                new ReactionAggregationMessage(event.getPartyroomId(), MessageTopic.REACTION_AGGREGATION,
                        new AggregationDto(event.getLikeCount(), event.getDislikeCount(), event.getGrabCount())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(PartyroomClosedEvent event) {
        messagePublisher.publish(MessageTopic.PARTYROOM_CLOSED.topic(),
                new PartyroomClosedMessage(event.getPartyroomId(), MessageTopic.PARTYROOM_CLOSED));
    }
}
