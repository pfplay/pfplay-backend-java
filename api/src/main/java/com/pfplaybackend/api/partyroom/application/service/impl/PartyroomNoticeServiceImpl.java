package com.pfplaybackend.api.partyroom.application.service.impl;

import com.pfplaybackend.api.partyroom.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.entity.domain.Partyroom;
import com.pfplaybackend.api.partyroom.event.EventPublisher;
import com.pfplaybackend.api.partyroom.presentation.payload.request.RegisterNoticeRequest;
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

    /**
     * 1. 공지사항 수정 가능 여부는 AOP에서 진행한다.
     * 2. AOP를 통해 PartyroomContext에 주입된 정보를 바탕으로 실행한다.
     * 3. PartyroomContext에 주입되는 정보는 다음과 같음
     * 3-1. 호출 유저 ID, 대상 유저 ID, PartyroomInfo
     * @param request
     * @return
     */
    @Override
    @Transactional
//    @PartyroomContextAop
    public void registerNotice(RegisterNoticeRequest request) {
        // 1. PartyInfo 객체 선언
        // Q1. PartyroomContext 내 정보에 대한 null 체크를 해야할까요??
//        PartyroomInfo partyroomInfo = PartyroomContext.getPartyInfo().orElseThrow(
//                () -> throw new Exception;
//        );

        // 2. PartyRoomData 객체 선언
//        PartyroomData partyroomData = partyroomRepository.getReferenceById(partyInfo.getPartyroomId());
        
        // 3. PartyroomDomain 객체 선언
//        Paryroom partyroom = partyroomData.toDomain();

        // 4. PartyroomDomain 객체 업데이트
        // Q2. updatePartyroom 객체를 새로 생성하는 이유가 있나요??                                  ---- CASE (1)
        // Q2-1. Partyroom 객체 자체를 업데이트하고, 업데이트 된 객체 자체를 넣는 방식은 지양해야 하나요??  ---- CASE (2)
        // Case (1)
//        Partyroom updatePartyroom = partyroom.updateNotice(request.getContent());
        // Case (2)
//        partyroom.updateNotice(request.getContent());

        // 5. PartyroomDomain 객체 업데이트 내역 반영
        // 5-1. Domain 객체 -> Data 객체 전환
        // 5-2. Repository 호출
        // Case (1)
//        partyroomRepository.save(updatePartyroom.toData());
        // Case (2)
//        partyroomRepository.save(partyroom.toData());

        // 6. Event Publishing
//        eventPublisher.publish("notice", request.getContent());
    }
}
