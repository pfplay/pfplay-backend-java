package com.pfplaybackend.api.partyroom.event;

public interface EventBroadcaster {

    public void broadcast(String Topic, String message);
}