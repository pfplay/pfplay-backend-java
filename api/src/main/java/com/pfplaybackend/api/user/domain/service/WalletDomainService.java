package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.user.domain.model.value.WalletAddress;
import org.springframework.stereotype.Service;

@Service
public class WalletDomainService {

    public WalletAddress verifyWalletSignature() {
        // TODO 전달된 지갑 주소에 대한 서명 검증
        return null;
    }
}
