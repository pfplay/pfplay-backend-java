package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DjDto {
    private int djId;
    private int orderNumber;
    private String nickname;
    private String avatarBodyUri;
    private String avatarFaceUri;
}