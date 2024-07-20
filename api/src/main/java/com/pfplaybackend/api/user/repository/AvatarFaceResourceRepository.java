package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarFaceResourceRepository extends JpaRepository<AvatarFaceResourceData, Long> {
    Optional<AvatarFaceResourceData> findByResourceUri(String resourceUri);
    AvatarFaceResourceData findOneAvatarResourceByResourceUri(String resourceUri);
}