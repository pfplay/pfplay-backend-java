package com.pfplaybackend.api.user.repository.custom;

import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.Optional;

public interface GuestRepositoryCustom {
    Optional<GuestData> findByUserId(UserId userId);
}
