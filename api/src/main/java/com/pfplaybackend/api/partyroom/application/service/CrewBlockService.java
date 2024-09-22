package com.pfplaybackend.api.partyroom.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewBlockService {

    // TODO 타 크루를 차단할 수 있다.

    // CHAT_BLOCK_PERMANENT
    // 타 크루를 차단한다는 것은 CrewId가 아닌 UserId 수준에서 차단이 되어야 한다.
    // 'A가 B를 차단했다.' 라는 것은 B의 메시지가 A에게 노출되지 않아야 한다.
    // 실제 시스템적으로는 클라이언트가 메시지를 수신할테지만 js 로직에 의해 '보이지 않게' 만들어야 한다.

}
