package com.pfplaybackend.api.user.repository.custom;

import com.pfplaybackend.api.user.domain.model.data.MemberData;
import com.pfplaybackend.api.user.domain.model.value.UserId;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<MemberData> findByUserId(UserId userId);
}
