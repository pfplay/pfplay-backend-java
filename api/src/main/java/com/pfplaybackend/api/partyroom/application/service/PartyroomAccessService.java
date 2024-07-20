package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartymemberConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AccessMessage;
import com.pfplaybackend.api.partyroom.event.message.TmpUser;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
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
        // 1. 해당 파티룸의 폐쇄 여부 확인
        // 2. 사용자가 '이미 다른 파티룸에서의 활동중' 여부 확인
        // 3. 해당 파티룸의 허용 가능 인원 수 초과 확인
        // 4. 해당 파티룸에 대한 '기존 참여 이력' 여부 확인
        // 5. 해당 파티룸에서의 영구 페널티 여부 확인
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // Partymember partymember = Partymember.create(partyContext.getUserId(), partyroomId, partyContext.getAuthorityTier(), GradeType.LISTENER);
        Partyroom updatedPartyroom = partyroom.createAndAddPartymember(partyContext.getUserId(), AuthorityTier.FM, GradeType.LISTENER);
        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));
    }

    public void enterByHost(UserId hostId, Partyroom partyroom) {
        // TODO Require Partyroom Object
        Partyroom updatedPartyroom = partyroom.createAndAddPartymember(hostId, AuthorityTier.FM, GradeType.HOST);
        PartyroomData partyroomData = partyroomConverter.toData(updatedPartyroom);
        partyroomRepository.save(partyroomData);
    }

    @Transactional
    public void exit(PartyroomId partyroomId) {
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        if(partyroomDomainService.isExistInDjQueue()) {
            // Dj 대기열에서 강제 제거
        }
        // isActive: false
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
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
