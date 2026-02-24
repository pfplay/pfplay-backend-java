package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.exception.http.UnauthorizedException;
import com.pfplaybackend.api.user.adapter.out.persistence.UserAccountRepository;
import com.pfplaybackend.api.user.application.dto.result.MyInfoResult;
import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoQueryService {

    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public MyInfoResult getMyInfo() {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        UserAccountData user = userAccountRepository.findByUserId(authContext.getUserId())
                .orElseThrow(() -> new UnauthorizedException("USER_NOT_FOUND", "User not found"));
        return MyInfoResult.from(user);
    }
}
