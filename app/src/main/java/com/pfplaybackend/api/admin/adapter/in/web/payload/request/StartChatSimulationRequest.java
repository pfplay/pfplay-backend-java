package com.pfplaybackend.api.admin.adapter.in.web.payload.request;

import com.pfplaybackend.api.admin.domain.enums.ChatScriptType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for starting chat simulation in a partyroom
 */
@Getter
@NoArgsConstructor
public class StartChatSimulationRequest {

    /**
     * Type of chat script to use for simulation
     * - CHILL: Relaxed, chill music appreciation messages
     * - HYPE: High-energy, excited party messages
     */
    @NotNull(message = "scriptType is required")
    private ChatScriptType scriptType;
}
