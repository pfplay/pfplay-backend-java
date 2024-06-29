package com.pfplaybackend.api.user.repository;


import com.pfplaybackend.api.user.domain.entity.data.AvatarResourceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvatarResourceRepository extends JpaRepository<AvatarResourceData, Long> {
    @Query("SELECT a FROM AvatarResourceData a WHERE a.isDefaultSetting = true")
    Optional<AvatarResourceData> getDefaultSettingResource();

    @Query("SELECT a FROM AvatarResourceData a")
    Optional<List<AvatarResourceData>> findAllAvatarResources();

    AvatarResourceData findOneAvatarResourceByResourceUri(String resourceUri);
}