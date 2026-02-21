package com.pfplaybackend.api.admin.adapter.in.web.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for simulating reactions in a partyroom
 *
 * Simulates 2 random crew members reacting to current playback:
 * - 1 crew member: LIKE reaction
 * - 1 crew member: GRAB reaction
 */
@Getter
@NoArgsConstructor
public class SimulateReactionsRequest {
    // Currently no parameters needed - uses random crew selection
    // Can be extended later to specify reaction types or crew count
}
