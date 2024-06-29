package com.pfplaybackend.api.partyroom.presentation.payload.response;


import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class EnterPartyroomResponse {
    private String uid;
    private AuthorityTier authorityTier;
    private String nickname;
    private long memberId;
    private GradeType gradeType;
    private boolean isHost;
}