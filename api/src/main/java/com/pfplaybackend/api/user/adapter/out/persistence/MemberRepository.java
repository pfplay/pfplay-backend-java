package com.pfplaybackend.api.user.adapter.out.persistence;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.adapter.out.persistence.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberData, Long>, MemberRepositoryCustom {
    Optional<MemberData> findByEmail(String email);

    long countByProviderType(ProviderType providerType);
}
