package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<ProfileData, Long> {
    List<ProfileData> findAllByUserIdIn(List<UserId> userIds);
    ProfileData findByUserId(UserId userIds);
}