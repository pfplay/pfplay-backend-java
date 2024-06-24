package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.dto.command.UpdateWalletCommand;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.domain.service.WalletDomainService;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWalletService {

    private final MemberRepository memberRepository;
    private final WalletDomainService walletDomainService;

    @Transactional
    public void updateMyWalletAddress(UpdateWalletCommand updateWalletCommand) {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        // Get UserId → Query 'Member' Object
        Member member = memberRepository.findByUserId(userContext.getUserId()).orElseThrow().toDomain();
        // TODO 2. 지갑 주소 서명 검증 실패 시 예외 발생!
        // WalletAddress verifiedWalletAddress =  walletDomainService.verifyWalletSignature();
        Member updatedMember = member.updateWalletAddress(new WalletAddress(updateWalletCommand.getWalletAddress()));
        memberRepository.save(updatedMember.toData());
    }
}
