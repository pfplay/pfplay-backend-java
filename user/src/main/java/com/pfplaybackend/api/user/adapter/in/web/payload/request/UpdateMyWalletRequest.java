package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateMyWalletRequest {
    @NotBlank(message = "walletAddress is required.")
    private String walletAddress;
}
