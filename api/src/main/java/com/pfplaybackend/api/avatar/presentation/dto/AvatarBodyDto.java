package com.pfplaybackend.api.avatar.presentation.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AvatarBodyDto {
    private final Long id;
    private final String type;
    private final String name;
    private final String image;
    private final Integer point;
}
