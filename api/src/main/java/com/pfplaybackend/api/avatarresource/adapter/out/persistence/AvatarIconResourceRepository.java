package com.pfplaybackend.api.avatarresource.adapter.out.persistence;

import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.enums.PairType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarIconResourceRepository extends JpaRepository<AvatarIconResourceData, Long> {
    AvatarIconResourceData findByNameAndPairType(String name, PairType pairType);
}
