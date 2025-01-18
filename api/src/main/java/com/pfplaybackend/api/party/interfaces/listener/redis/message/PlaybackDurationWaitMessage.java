package com.pfplaybackend.api.party.interfaces.listener.redis.message;

import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackDurationWaitMessage implements Serializable {
    private PartyroomId partyroomId;
    private UserId userId;
}
