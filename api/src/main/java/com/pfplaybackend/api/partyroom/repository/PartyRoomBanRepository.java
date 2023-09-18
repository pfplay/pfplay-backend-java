package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.entity.PartyRoomBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyRoomBanRepository extends JpaRepository<PartyRoomBan, Long> {

    List<PartyRoomBan> findAllByPartyRoomId(Long partyRoomId);

}
