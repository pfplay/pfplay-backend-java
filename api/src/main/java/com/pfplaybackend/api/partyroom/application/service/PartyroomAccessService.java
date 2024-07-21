package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberSummaryDto;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartymemberConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AccessMessage;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartymemberConverter partymemberConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserProfileService userProfileService;

    // TODO Publish event only when the transaction is successful
    // AccessMessage message = new AccessMessage(new PartyroomId(50L));
    // redisMessagePublisher.publish(MessageTopic.ACCESS, message);
    @Transactional
    public void tryEnter(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // 1. 해당 파티룸의 폐쇄 여부 확인
        // 2. 사용자가 '이미 다른 파티룸에서의 활동중' 여부 확인
        // 3. 해당 파티룸의 허용 가능 인원 수 초과 확인
        // 4. 해당 파티룸에 대한 '기존 참여 이력' 여부 확인
        // 5. 해당 파티룸에서의 영구 페널티 여부 확인
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        // Add Party Members through Party Rooms
        UserId userId = partyContext.getUserId();
        Partyroom updatedPartyroom = partyroom.addNewPartymember(userId, partyContext.getAuthorityTier(), GradeType.LISTENER);
        PartyroomData data = partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));

        // Added Partymember who has id after transaction
        Partyroom partyroomUpdated = partyroomConverter.toDomain(data);
        Partymember partymember = partyroomUpdated.getPartymemberByUserId(userId);
        ProfileSettingDto dto = userProfileService.getUserProfileSetting(userId);

        PartymemberSummaryDto partymemberSummaryDto = PartymemberSummaryDto.from(partymember, dto);
        AccessMessage accessMessage = new AccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.ACCESS,
                AccessType.ENTER,
                partymemberSummaryDto);
        redisMessagePublisher.publish(MessageTopic.ACCESS, accessMessage);
    }

    public void enterByHost(UserId hostId, Partyroom partyroom) {
        Partyroom updatedPartyroom = partyroom.addNewPartymember(hostId, AuthorityTier.FM, GradeType.HOST);
        PartyroomData partyroomData = partyroomConverter.toData(updatedPartyroom);
        partyroomRepository.save(partyroomData);
    }

    @Transactional
    public void exit(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // TODO 존재할 시 'Dj 대기열'에서 강제 제거
        //
        Partymember partymember = partyroom.deactivatePartymemberAndGet(partyContext.getUserId());
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        PartymemberSummaryDto partymemberSummaryDto = new PartymemberSummaryDto(partymember.getId());
        AccessMessage accessMessage = new AccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.ACCESS,
                AccessType.EXIT,
                partymemberSummaryDto);
        redisMessagePublisher.publish(MessageTopic.ACCESS, accessMessage);
    }

    @Transactional
    public void forceOut() {
        if(partyroomDomainService.isExistInDjQueue()) {
            // Dj 대기열에서 강제 제거
        }
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
    }
}
