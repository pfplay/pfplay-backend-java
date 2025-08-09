package com.pfplaybackend.api.auth.application.store;

public interface StateStore {
    String generateAndStoreState(String provider);
    boolean validateAndConsumeState(String state, String expectedProvider);
}
