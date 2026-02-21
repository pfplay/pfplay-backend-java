package com.pfplaybackend.api.party.application.dto.command;

public record UpdatePartyroomCommand(String title, String introduction, String linkDomain, int playbackTimeLimit) {}
