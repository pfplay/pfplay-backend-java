package com.pfplaybackend.api.user.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateWalletCommand {
    private String walletAddress;
}
