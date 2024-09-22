package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewProfilePreCheckMessage implements Serializable {
    private UserId userId;
    private String nickname;
    private String avatarFaceUri;
    private String avatarBodyUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;
}
