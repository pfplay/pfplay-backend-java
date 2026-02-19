package com.pfplaybackend.api.auth.application.port.out;

public interface StateStorePort {
    String generateAndStoreState(String provider);
    boolean validateAndConsumeState(String state, String expectedProvider);
}
