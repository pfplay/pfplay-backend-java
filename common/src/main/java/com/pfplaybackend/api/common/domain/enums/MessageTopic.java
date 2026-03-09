package com.pfplaybackend.api.common.domain.enums;

public enum MessageTopic {
    PLAYBACK_DEACTIVATED,
    CREW_ENTERED,
    CREW_EXITED,
    REACTION_AGGREGATION_UPDATED,
    REACTION_PERFORMED,
    CREW_GRADE_CHANGED,
    CREW_PENALIZED,
    CREW_PROFILE_CHANGED,
    CREW_PROFILE_PRE_CHECK,
    PLAYBACK_STARTED,
    DJ_QUEUE_CHANGED,
    CHAT_MESSAGE_SENT,
    PARTYROOM_CLOSED;

    public String topic() {
        return name().toLowerCase();
    }
}
