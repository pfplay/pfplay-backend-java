package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomInfoService {

    private final PartyroomRepository partyroomRepository;

    public void getAllPartyrooms() {

    }

    public void getSummaryInfo(PartyroomId partyroomId) {
        // TODO
        // TODO
        // 파티원들의 아바타 정보를 어떻게 가져와야할까?

        // TODO 입장 시 입장자의 아바타 설정 정보를 Redis 에 저장
    }


    public void getGeneralInfo(PartyroomId partyroomId) {

    }

    public void getPartymembers(PartyroomId partyroomId) {

    }
}
