package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PartyroomManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomDomainService partyroomDomainService;

    public Partyroom createMainStage() {
        String title = "메인 스테이지";
        String description = "이곳은 메인 스테이지입니다.";
        String suffixUri = "main";
        partyroomDomainService.isLinkAddressDuplicated(suffixUri);
//        Partymember partymember = Partymember.create();
//        Partyroom partyroom = Partyroom.create(title, description, suffixUri, partymember, StageType.MAIN);
//        partyroomRepository.save(partyroom.toData());
        return null;
    }

    public Partyroom createGeneralPartyRoom(CreatePartyroomRequest createPartyroomRequest) {
        try {
            // AuthorityType.FM
            partyroomDomainService.isQualifiedToCreatePartyroom();
            String title = createPartyroomRequest.getTitle();
            String description = createPartyroomRequest.getDescription();
            String suffixUri = createPartyroomRequest.getSuffixUri();
            partyroomDomainService.isLinkAddressDuplicated(suffixUri);
//            Partymember partymember = Partymember.create();
//            Partyroom partyroom = Partyroom.create(title, description, suffixUri, partymember, StageType.GENERAL);
//            partyroomRepository.save(partyroom.toData());
            return null;
        }catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    @Transactional
    public void deletePartyRoom(PartyroomId partyroomId) {
        // TODO 클라이언트가 파티룸의 호스트가 맞는가?
        // TODO 파티룸에 자신을 제외한 멤버가 없는가?
    }

    @Transactional
    public void updateDjQueueStatus(PartyroomId partyroomId, UpdateDjQueueStatusRequest request) {

    }
}
