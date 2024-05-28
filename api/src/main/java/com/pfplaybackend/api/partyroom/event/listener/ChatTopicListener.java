package com.pfplaybackend.api.partyroom.event.listener;

import com.pfplaybackend.api.partyroom.event.EventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChatTopicListener extends AbstractTopicListener {

    private final EventBroadcaster eventBroadcaster;

    @Override
    public void handleMessage(String message) {
        System.out.println("Received message: " + message);
        // Convert Logic
        // ...

        // Call Bridge Message
    }
}
