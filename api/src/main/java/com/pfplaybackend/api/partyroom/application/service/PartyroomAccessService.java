package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.model.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.event.EventPublisher;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final PartyroomRepository partyroomRepository;
    // TODO
    private final UserProfileService userProfileService;
    private final PartyroomDomainService partyroomDomainService;
    private final EventPublisher eventPublisher;

    // TODO Publish event only when the transaction is successful
    @Transactional
    public void enter() {
        // TODO 사용자가 이미 다른 파티룸에 위치 중이라면 실패 처리
        Partymember partymember = new Partymember();
        if(partyroomDomainService.isNotInPartyroom()) {
            //
        }
        try {
            // TODO 입장 인원 수 제약 확인
            // Partyroom updatedPartyroom = partyroom.enter(partymember);
            // PartyroomData partyroomData = updatedPartyroom.toData();
            // TODO 파티 멤버의 아바타 설정 데이터 확인 (호출 필요)
            // TODO Call userProfileService
            // 해당 값은 Redis에 '저장'한다.

            // partyroomRepository.save(partyroomData);
            // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    @Transactional
    public void exit() {
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        if(partyroomDomainService.isExistInDJQueue()) {
            // DJQueue에서 제거
        }
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
    }

    @Transactional
    public void forceOut() {
        if(partyroomDomainService.isExistInDJQueue()) {
            // DJQueue에서 제거
        }
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
    }
}
