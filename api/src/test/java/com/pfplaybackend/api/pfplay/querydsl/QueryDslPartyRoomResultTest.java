package com.pfplaybackend.api.pfplay.querydsl;

import com.pfplaybackend.api.common.QueryDslConfig;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.*;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.presentation.dto.*;
import com.pfplaybackend.api.partyroom.repository.PartyRoomJoinRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.partyroom.repository.dsl.PartyRoomJoinRepositorySupport;
import com.pfplaybackend.api.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.pfplaybackend.api.entity.QPartyRoomJoin.partyRoomJoin;
import static com.pfplaybackend.api.entity.QPartyRoom.partyRoom;
import static com.pfplaybackend.api.entity.QPartyRoomBan.partyRoomBan;
import static com.pfplaybackend.api.entity.QPartyPermission.partyPermission;

@SpringBootTest
@ActiveProfiles("test")
public class QueryDslPartyRoomResultTest {
    @Autowired
    QueryDslConfig queryFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PartyRoomRepository partyRoomRepository;

    @Autowired
    PartyRoomJoinRepository partyRoomJoinRepository;

    @Autowired
    ObjectMapperConfig om;

    @Autowired
    PartyRoomJoinRepositorySupport partyRoomJoinRepositorySupport;

    User admin;
    User attend;
    PartyRoom partyRoom;
    PartyRoomJoin partyRoomJoin;

    void init() {
        admin = User.builder()
                        .authority(Authority.ROLE_USER)
                        .email("pfplay.io@gmail.com")
                        .build();

        attend =
                User.builder()
                        .authority(Authority.ROLE_USER)
                        .email("pfplay.io.test@gmail.com")
                        .build();

        userRepository.saveAll(
                Arrays.asList(admin, attend)
        );

        partyRoom = partyRoomRepository.save(PartyRoom.builder()
                .name("뉴진스 노래 모음~~")
                .domain("https://pfplay.io")
                .status(PartyRoomStatus.ACTIVE)
                .djingLimit(3)
                .introduce("뉴진스~~~")
                .type(PartyRoomType.PARTY)
                .user(admin)
                .build()
        );

        partyRoomJoin = partyRoomJoinRepository.save(PartyRoomJoin
                .builder()
                .partyRoom(partyRoom)
                .partyRoomBan(null)
                .user(attend)
                .role(PartyPermissionRole.LISTENER)
                .active(PartyRoomStatus.ACTIVE)
                .build()
        );

    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void partyRoomJoinRepositorySupportResult() {
        Long roomId = partyRoom.getId();
        Long userId = attend.getId();
        PartyRoomJoinResultDto partyRoomJoinResultDto = partyRoomJoinRepositorySupport
                .findByRoomIdWhereJoinRoom(roomId, userId).get();

        Assertions.assertEquals(partyRoomJoinResultDto.getPartyRoom().getId(), roomId);
        Assertions.assertNull(partyRoomJoinResultDto.getPartyRoomBan());
    }


}
