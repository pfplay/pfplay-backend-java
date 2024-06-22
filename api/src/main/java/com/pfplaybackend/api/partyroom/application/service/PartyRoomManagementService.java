package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.model.enums.PartyroomType;
import com.pfplaybackend.api.partyroom.domain.model.value.LinkAddress;
import com.pfplaybackend.api.partyroom.domain.model.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PartyRoomManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomDomainService partyroomDomainService;

    public Partyroom createMainStage() {
        String title = "메인 스테이지";
        String description = "이곳은 메인 스테이지입니다.";
        String suffixUri = "main";
        partyroomDomainService.isLinkAddressDuplicated(suffixUri);
        Partymember partymember = Partymember.create();
        Partyroom partyroom = Partyroom.create(title, description, suffixUri, partymember, PartyroomType.MAIN);
        partyroomRepository.save(partyroom.toData());
        return partyroom;
    }

    public Partyroom createGeneralPartyRoom(CreatePartyroomRequest createPartyroomRequest) {
        try {
            // AuthorityType.FM
            partyroomDomainService.isQualifiedToCreatePartyroom();
            String title = createPartyroomRequest.getTitle();
            String description = createPartyroomRequest.getDescription();
            String suffixUri = createPartyroomRequest.getSuffixUri();
            partyroomDomainService.isLinkAddressDuplicated(suffixUri);
            Partymember partymember = Partymember.create();
            Partyroom partyroom = Partyroom.create(title, description, suffixUri, partymember, PartyroomType.GENERAL);
            partyroomRepository.save(partyroom.toData());
            return partyroom;
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
}
