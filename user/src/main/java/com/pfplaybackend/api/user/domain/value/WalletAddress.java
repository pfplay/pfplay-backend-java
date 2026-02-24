package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class WalletAddress {

    @Column(name = "wallet_address")
    private String value;

    public WalletAddress() {
        this.value = "";
    }

    public WalletAddress(String value) {
        this.value = value;
    }
}
