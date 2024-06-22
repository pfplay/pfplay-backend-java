package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@Getter
public class WalletAddress {
    private String walletAddress;

    public WalletAddress() {
        this.walletAddress = "";
    }

    public WalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
