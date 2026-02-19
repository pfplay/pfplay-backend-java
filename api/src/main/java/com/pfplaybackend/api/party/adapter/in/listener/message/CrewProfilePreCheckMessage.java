package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;
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
    private AvatarCompositionType avatarCompositionType;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;
}
