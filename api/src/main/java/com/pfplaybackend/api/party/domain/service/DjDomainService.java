package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.infrastructure.repository.DjRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DjDomainService {

    private final DjRepository djRepository;

    public boolean isExistDj(Long partyroomId) {
        return djRepository.existsByPartyroomDataIdAndIsQueuedTrue(partyroomId);
    }
}
