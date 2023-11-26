package com.pfplaybackend.api.pfplay.partyroom;
import com.pfplaybackend.api.entity.PartyRoomJoin;
import com.pfplaybackend.api.partyroom.repository.PartyRoomJoinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PartyRoomTestV2 {

    @Autowired
    private PartyRoomJoinRepository repository;

    @Test
    public void 테스트() {
        repository.countPartyRoomJoinByPartyRoomId(2L);
    }
}
