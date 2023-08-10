package com.pfplaybackend.api.guest.presentation.response;

import lombok.Data;

@Data
public class GuestCreateResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    private final String authority;
}
