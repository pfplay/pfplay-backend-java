package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.application.dto.command.CrewProfileChangedCommand;
import com.pfplaybackend.api.party.application.dto.command.CrewProfilePreCheckCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User 도메인에서의 프로필(닉네임, 아바타 등) 설정 변경 시 호출되는 핸들러.
 * 같은 파티 멤버들에게 해당 변경 상태를 통지한다.
 */
@Service
@RequiredArgsConstructor
public class CrewProfileChangeEventHandler {

    private final PartyroomQueryService partyroomQueryService;

    public Optional<CrewProfileChangedCommand> preCheck(CrewProfilePreCheckCommand command) {
        Optional<ActivePartyroomDto> optional = partyroomQueryService.getMyActivePartyroom(command.userId());
        return optional.map(dto -> new CrewProfileChangedCommand(
                new PartyroomId(dto.id()), dto.crewId(),
                command.nickname(), command.avatarCompositionType(),
                command.avatarBodyUri(), command.avatarFaceUri(), command.avatarIconUri(),
                command.combinePositionX(), command.combinePositionY(),
                command.offsetX(), command.offsetY(), command.scale()));
    }
}
