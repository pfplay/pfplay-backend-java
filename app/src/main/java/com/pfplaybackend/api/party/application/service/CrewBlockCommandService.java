package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.exception.BlockException;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewBlockHistoryRepository;
import com.pfplaybackend.api.party.application.dto.command.AddBlockCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewBlockCommandService {

    private final PartyroomAggregatePort aggregatePort;
    private final CrewBlockHistoryRepository blockHistoryRepository;
    private final PartyroomQueryService partyroomQueryService;

    @Transactional
    public void addBlock(AddBlockCommand command) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ActivePartyroomDto dto = partyroomQueryService.getMyActivePartyroomOrThrow(authContext.getUserId());

        CrewId blockerCrewId = new CrewId(dto.crewId());
        CrewId blockedCrewId = new CrewId(command.crewId());
        Optional<CrewBlockHistoryData> historyDataOptional = blockHistoryRepository.findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(blockerCrewId, blockedCrewId);
        if(historyDataOptional.isPresent()) throw ExceptionCreator.create(BlockException.ALREADY_BLOCKED_CREW);

        CrewData blockedCrew = aggregatePort.findCrewById(blockedCrewId.getId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewBlockHistoryData historyData = CrewBlockHistoryData.builder()
                .blockerCrewId(blockerCrewId)
                .blockedCrewId(blockedCrewId)
                .blockedUserId(blockedCrew.getUserId())
                .blockDate(LocalDateTime.now())
                .unblocked(false)
                .build();

        blockHistoryRepository.save(historyData);
    }

    @Transactional
    public void removeBlock(Long blockId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ActivePartyroomDto dto = partyroomQueryService.getMyActivePartyroomOrThrow(authContext.getUserId());

        CrewId blockerCrewId = new CrewId(dto.crewId());
        CrewBlockHistoryData historyData  = blockHistoryRepository.findByIdAndBlockerCrewIdAndUnblockedIsFalse(blockId, blockerCrewId)
                .orElseThrow(() -> ExceptionCreator.create(BlockException.BLOCK_HISTORY_NOT_FOUND));

        historyData.unblock();

        blockHistoryRepository.save(historyData);
    }
}
