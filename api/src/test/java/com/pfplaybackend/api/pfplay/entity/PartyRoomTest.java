package com.pfplaybackend.api.pfplay.entity;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PartyRoomTest {

    private PartyRoomRepository partyRoomRepository;
    private UserRepository userRepository;

    @Autowired
    public PartyRoomTest(PartyRoomRepository partyRoomRepository, UserRepository userRepository) {
        this.partyRoomRepository = partyRoomRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void createPartyRoom() {
        User user = User.builder()
                .authority(Authority.ROLE_USER)
                .email("pfplay.io@gmail.com")
                .build();

        User saveUser = userRepository.save(user);

        PartyRoom partyRoom = PartyRoom.builder()
                .name("뉴진스 노래 모음")
                .domain("https://pfplay.io")
                .status(PartyRoomStatus.ACTIVE)
                .type(PartyRoomType.PRIVATE)
                .djingLimit(4)
                .introduce("뉴진스 노래를 즐겨보세요.")
                .user(saveUser)
                .build();

        partyRoomRepository.save(partyRoom);
    }

    public void getPartyRoomInfo() {
        userRepository.findById("1");
    }
}