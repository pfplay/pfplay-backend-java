package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMyBioRequest {
    @Size(max = 20, message = "닉네임은 20자를 초과할 수 없습니다")
    String nickname;

    @Size(max = 50, message = "소개글은 50자를 초과할 수 없습니다")
    String introduction;
}
