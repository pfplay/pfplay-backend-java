package com.pfplaybackend.api.pfplay.guest;

import com.pfplaybackend.api.common.util.CustomStringUtils;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.Guest;
import com.pfplaybackend.api.guest.presentation.request.GuestCreateRequest;
import com.pfplaybackend.api.guest.repository.GuestRepository;
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
class GuestTest {

    private GuestRepository guestRepository;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapperConfig om;

    @Autowired
    public GuestTest(GuestRepository guestRepository, MockMvc mockMvc) {
        this.guestRepository = guestRepository;
        this.mockMvc = mockMvc;
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void createGuest() {

        final String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.46";
        final String uuid = CustomStringUtils.getRandomUuidWithoutHyphen().substring(0, 6);
        final String GUEST_NAME_PREFIX = "Guest_";

        Guest guest = Guest.builder()
                .name(GUEST_NAME_PREFIX + uuid)
                .agent(agent)
                .build();

        Guest guestSave = guestRepository.save(guest);
        Assertions.assertEquals(guestSave.getName(), guest.getName());
    }

    @Test
    public void createGuestMock() throws Exception {
        final String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.46";
        String content = om.mapper().writeValueAsString(new GuestCreateRequest(agent));

        mockMvc.perform(post("/api/v1/guest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

}