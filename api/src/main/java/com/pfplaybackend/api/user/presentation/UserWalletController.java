package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.dto.command.UpdateWalletCommand;
import com.pfplaybackend.api.user.application.service.UserWalletService;
import com.pfplaybackend.api.user.presentation.payload.request.UpdateMyWalletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Profile API", description = "Operations related to user's profile management")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserWalletController {

    private final UserWalletService userWalletService;

    /**
     * 호출한(인증된) 사용자의 프로필 리소스 내 Wallet 리소스를 갱신한다.
     * @param request
     * @return
     */
    @PutMapping("/me/profile/wallet")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> updateMyWallet(@RequestBody UpdateMyWalletRequest request) {
        userWalletService.updateMyWalletAddress(UpdateWalletCommand.builder()
                .walletAddress(request.getWalletAddress())
                .build()
        );
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
