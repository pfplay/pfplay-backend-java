package com.pfplaybackend.api.profile.adapter.out.persistence;

import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<ProfileData, Long> {
    List<ProfileData> findAllByUserIdIn(List<UserId> userIds);
    ProfileData findByUserId(UserId userId);
}