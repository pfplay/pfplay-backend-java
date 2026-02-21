package com.pfplaybackend.api.party.adapter.in.stomp.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IncomingGroupChatMessage {
    private String content;
}
