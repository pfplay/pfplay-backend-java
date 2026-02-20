package com.pfplaybackend.api.user.adapter.out.persistence;

import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountData, UserId> {
    Optional<UserAccountData> findByUserId(UserId userId);
}
