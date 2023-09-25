package com.pfplaybackend.api.pfplay.partyroom;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.presentation.request.PartyRoomCreateRequest;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class PartyRoomTest {

    private PartyRoomRepository partyRoomRepository;
    private UserRepository userRepository;
    private MockMvc mockMvc;
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapperConfig om;

    @Autowired
    public PartyRoomTest(PartyRoomRepository partyRoomRepository, UserRepository userRepository, MockMvc mockMvc, TokenProvider tokenProvider) {
        this.partyRoomRepository = partyRoomRepository;
        this.userRepository = userRepository;
        this.mockMvc = mockMvc;
        this.tokenProvider = tokenProvider;
    }

    @Test
    @Transactional
    @Rollback(false)
    public void createPartyRoom() {
        User user = User.builder()
                .authority(Authority.ROLE_USER)
                .email("pfplay.io@gmail.com")
                .build();

        User saveUser = userRepository.save(user);

        PartyRoom partyRoom = PartyRoom.builder()
                .name("뉴진스 노래 모음~~")
                .domain("https://pfplay.io")
                .status(PartyRoomStatus.ACTIVE)
                .djingLimit(3)
                .introduce("뉴진스~~~")
                .type(PartyRoomType.PARTY)
                .user(user)
                .build();

        partyRoomRepository.save(partyRoom);
        Assertions.assertEquals(partyRoom.getUser().getId(), user.getId());
    }

    @Test
    @Transactional
    public void getPartyRoomInfo() {
        Long userId = userRepository.findById(1L).orElseThrow().getId();
        PartyRoom partyRooms = partyRoomRepository.findByUserId(userId);

        Assertions.assertEquals(partyRooms.getId(), 1);
        Assertions.assertEquals(partyRooms.getUser().getId(), 1);
    }

    @Test
    public void createPartyRoomMockMvc() throws Exception {
        User user = User.builder()
                .email("test@1234.com")
                .authority(Authority.ROLE_USER)
                .build();

        User save = userRepository.save(user);

        String accessToken = tokenProvider.createAccessToken(save.getAuthority(), save.getEmail(), save.getId());
        String content = om.mapper().writeValueAsString(new PartyRoomCreateRequest(
                "뉴진스", "뉴진스 소개", "domain", 3
        ));

        System.out.println(accessToken);
        mockMvc.perform(post("/api/v1/party-room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

}