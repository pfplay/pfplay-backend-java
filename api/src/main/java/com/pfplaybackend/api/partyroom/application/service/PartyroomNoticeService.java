package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomNoticeService {

    private final RedisMessagePublisher redisMessagePublisher;
    private final PartyroomRepository partyroomRepository;

    @Transactional
    void updateNotice() {
        // 여기에 진입했으면 이미 AOP에서 공지사항 수정이 가능한
        // 권한이 가진 유저라고 판단을 한 이후다.

        // PartyInfo partyInfo PartyContext.getPartyInfo();
        // PartyroomId partyroomId = partyInfo.getPartyroomId();

//        PartyroomData partyroomData = partyroomRepository.getReferenceById(partyroomId);
//        Paryroom partyroom = partyroomData.toDomain();
//
//        Partyroom updatePartyroom = partyroom.updateNotice();
//        partyroomRepository.save(updatePartyroom.toData());
//
//        //
//        eventPublisher.publish("notice", "수정된 공지사항 내용");
    }
}
