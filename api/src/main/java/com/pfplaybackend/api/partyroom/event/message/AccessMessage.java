package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessMessage implements Serializable {
    private PartyroomId partyroomId;
    // private TmpUser tmpUser;
}
