package com.pfplaybackend.api.avatarresource.repository;


import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvatarBodyResourceRepository extends JpaRepository<AvatarBodyResourceData, Long> {
    @Query("SELECT a FROM AvatarBodyResourceData a WHERE a.isDefaultSetting = true")
    Optional<AvatarBodyResourceData> getDefaultSettingResource();

    @Query("SELECT a FROM AvatarBodyResourceData a")
    Optional<List<AvatarBodyResourceData>> findAllAvatarResources();

    AvatarBodyResourceData findOneAvatarResourceByResourceUri(String resourceUri);
}