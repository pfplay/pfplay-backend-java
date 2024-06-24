package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberData, UUID>, MemberRepositoryCustom {
    Optional<MemberData> findByEmail(String email);
}
