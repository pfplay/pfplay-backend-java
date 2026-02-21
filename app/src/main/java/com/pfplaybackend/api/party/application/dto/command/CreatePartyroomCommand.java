package com.pfplaybackend.api.party.application.dto.command;

public record CreatePartyroomCommand(String title, String introduction, String linkDomain, int playbackTimeLimit) {}
