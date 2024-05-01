package com.pfplaybackend.api.user.model.domain.operations;

import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.domain.ProfileDomain;
import com.pfplaybackend.api.user.model.entity.Profile;

public interface MemberOperation {
    MemberDomain updateActivity();
    MemberDomain updateProfile(ProfileDomain profileDomain);
    MemberDomain upgradeAuthorityTier();
}