package com.pfplaybackend.api.partyroom.application.service.impl;

import com.pfplaybackend.api.partyroom.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.entity.domain.Partyroom;
import com.pfplaybackend.api.partyroom.event.EventPublisher;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import jdk.jfr.TransitionTo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomNoticeServiceImpl implements PartyroomNoticeService {

    private final EventPublisher eventPublisher;
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
