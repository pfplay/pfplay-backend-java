package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarFaceResourceRepository extends JpaRepository<AvatarFaceResourceData, Long> {
}
