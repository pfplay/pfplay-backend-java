package com.pfplaybackend.api.user.repository.custom;

import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.model.value.UserId;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findCustomQueryMethod(UserId userId);
}
