package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarIconResourceRepository extends JpaRepository<AvatarIconResourceData, Long> {
}
