package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AccessMessage;
import com.pfplaybackend.api.partyroom.event.message.TmpUser;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final PartyroomRepository partyroomRepository;
    private final UserProfileService userProfileService;
    private final PartyroomDomainService partyroomDomainService;
    private final RedisMessagePublisher redisMessagePublisher;

    // TODO Publish event only when the transaction is successful
    // AccessMessage message = new AccessMessage(new PartyroomId(50L));
    // redisMessagePublisher.publish(MessageTopic.ACCESS, message);
    @Transactional
    public void tryEnter() {
        // 1. 해당 파티룸의 폐쇄 여부 확인
        // 2. 사용자가 이미 다른 파티룸에서의 활동중 여부 확인
        // 3. 해당 파티룸의 허용 가능 인원 수 초과 확인
        // 4. 해당 파티룸에서의 영구 페널티 여부 확인
//        Partymember partymember = new Partymember();
//        if(partyroomDomainService.isNotInPartyroom()) {
//            //
//        }
//        try {
//            // TODO 입장 인원 수 제약 확인
//            // Partyroom updatedPartyroom = partyroom.enter(partymember);
//            // PartyroomData partyroomData = updatedPartyroom.toData();
//            // TODO 파티 멤버의 아바타 설정 데이터 확인 (호출 필요)
//            // TODO Call userProfileService
//            // 해당 값은 Redis에 '저장'한다.
//
//            // partyroomRepository.save(partyroomData);
//            // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
//        } catch (Exception e) {
//            System.out.println(Arrays.toString(e.getStackTrace()));
//        }
    }

    @Transactional
    public void exit() {
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
