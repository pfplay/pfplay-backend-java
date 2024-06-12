package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.model.data.GuestData;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import com.pfplaybackend.api.user.repository.custom.GuestRepositoryCustom;
import com.pfplaybackend.api.user.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<GuestData, UUID>, GuestRepositoryCustom {
    Optional<GuestData> findGuestByUserId(UserId userId);
}