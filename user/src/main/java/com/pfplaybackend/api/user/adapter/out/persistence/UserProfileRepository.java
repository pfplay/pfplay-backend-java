package com.pfplaybackend.api.user.adapter.out.persistence;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<ProfileData, Long> {
    List<ProfileData> findAllByUserIdIn(List<UserId> userIds);
    ProfileData findByUserId(UserId userId);
}