package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 해당 서비스는 User 도메인에서의 아바타 설정 변경 시 호출되는 내용이다.
 *
 */
@Service
@RequiredArgsConstructor
public class PartymemberAvatarUpdateService {

    private RedisTemplate<String, Object> redisTemplate;
    private PartyroomRepository partyroomRepository;

    public void updatePartymemberAvatar() {
        // TODO Check if User is located within partyroom
        // 1. within partyroom
        // Get PartyroomId
        // → Update Redis info
        // → Propagate Topic Event!
        // 2. not within partyroom
        // return;
    }
}
