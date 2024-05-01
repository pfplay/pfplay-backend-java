package com.pfplaybackend.api.user.model.domain.operations;

import com.pfplaybackend.api.user.model.domain.MemberDomain;

public interface GuestOperation {
    MemberDomain updateProfile(MemberDomain memberDomain);

}
