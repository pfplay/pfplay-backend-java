package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PartyroomAccessQueryService {

    private final PartyroomAggregatePort aggregatePort;

    @Transactional(readOnly = true)
    public Map<String, Long> getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = aggregatePort.findByLinkDomain(LinkDomain.of(linkDomain))
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return Map.of(
                "partyroomId", partyroomData.getId()
        );
    }
}
