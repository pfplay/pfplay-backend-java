package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyplay.exception.GradeException;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomDomainService {

    private final PartyroomRepository partyroomRepository;

    public boolean isActiveInAnotherRoom(PartyroomId targetRoomId, PartyroomId activeRoomId) {
        return !targetRoomId.equals(activeRoomId);
    }

    // 파티룸이 재생 활성화 되었는지 여부 확인
    public boolean isChangedToActivation() {
        return true;
    }

    // 현재 유저가 해당 파티룸에 존재하는지 확인
    public void checkIsNotInPartyroom() {}

    // DJ 대기열에 존재하는지 여부 확인
    public boolean isExistInDjQueue() {
        return true;
    }

    // 파티룸을 생성할 수 있는 권한을 보유했는지 확인
    public void checkIsQualifiedToCreate(AuthorityTier authorityTier) {
        if(!authorityTier.equals(AuthorityTier.FM)) {
            throw ExceptionCreator.create(PartyroomException.RESTRICTED_AUTHORITY);
        }
    }

    // TODO 구현 필요
    // 링크 주소가 중복되었는지 확인
    public void checkIsLinkAddressDuplicated(String linkAddress) {

    }

    // 파티룸의 호스트가 맞는지 확인
    public void checkIsHost(Partyroom partyroom, UserId userId) {
        if(!partyroom.getHostId().equals(userId)) {
            throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        }
    }
}
