package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.response.PartyroomSharedLinkResponse;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PartyroomSharedLinkService {
    @Value("${shared-link.partyroom.uri-prefix}")
    private String SHARED_LINK_URI_PREFIX;

    @Value("${shared-link.partyroom.web-uri}")
    private String WEB_SERVER_ADDRESS;

    private final PartyroomConverter partyroomConverter;
    private final PartyroomRepository partyroomRepository;

    @Transactional(readOnly = true)
    public PartyroomSharedLinkResponse getSharedLink(Long partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId)
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        return PartyroomSharedLinkResponse.builder()
                .sharedLink(partyroom.getSharedLink(SHARED_LINK_URI_PREFIX))
                .build();
    }

    @Transactional(readOnly = true)
    public URI getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return URI.create(WEB_SERVER_ADDRESS + "/" + partyroomData.getPartyroomId());
    }
}
