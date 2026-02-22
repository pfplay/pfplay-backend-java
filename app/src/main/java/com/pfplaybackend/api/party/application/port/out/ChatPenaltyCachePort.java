package com.pfplaybackend.api.party.application.port.out;

public interface ChatPenaltyCachePort {
    void recordChatBan(Long crewId, int durationSeconds);
    boolean isChatBanned(Long crewId);
}
