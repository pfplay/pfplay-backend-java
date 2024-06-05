package com.pfplaybackend.api.partyroom.event.listener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class AbstractTopicListener {

    abstract public void handleMessage(String message);
}
