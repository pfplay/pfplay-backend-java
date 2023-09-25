package com.pfplaybackend.api.pfplay.partyroom;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.PartyRoomBan;
import com.pfplaybackend.api.partyroom.repository.PartyRoomBanRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class PartyRoomBanTest {

    PartyRoomRepository partyRoomRepository;
    PartyRoomBanRepository partyRoomBanRepository;

    @Autowired
    public PartyRoomBanTest(PartyRoomRepository partyRoomRepository, PartyRoomBanRepository partyRoomBanRepository) {
        this.partyRoomRepository = partyRoomRepository;
        this.partyRoomBanRepository = partyRoomBanRepository;
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void createPartyRoomBan() {
        PartyRoom partyRoom = partyRoomRepository.findAll().get(0);
        PartyRoomBan partyRoomBan = PartyRoomBan.builder()
                .userId(partyRoom.getUser().getId())
                .partyRoom(partyRoom)
                .ban(false)
                .kick(false)
                .chat(false)
                .reason(null)
                .authority(partyRoom.getUser().getAuthority())
                .build();

        partyRoomBanRepository.save(partyRoomBan);
        List<PartyRoomBan> allByPartyRoomId = partyRoomBanRepository.findAllByPartyRoomId(partyRoom.getId());
    }
}
