package com.pfplaybackend.api.playlist.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class ListDeleteResponse {
    private final List<Long> listIds;
}
