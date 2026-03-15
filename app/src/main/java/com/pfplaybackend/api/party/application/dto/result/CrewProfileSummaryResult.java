package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CrewProfileSummaryResult(
        @Schema(example = "1") Long crewId,
        @Schema(example = "DJ_Master") String nickname,
        @Schema(example = "Music lover") String introduction,
        @Schema(example = "https://cdn.pfplay.xyz/avatar/body/default.png") String avatarBodyUri,
        @Schema(example = "0") int combinePositionX,
        @Schema(example = "0") int combinePositionY,
        @Schema(example = "https://cdn.pfplay.xyz/avatar/face/smile.png") String avatarFaceUri,
        List<ActivitySummaryDto> activitySummaries
) {
    public static CrewProfileSummaryResult from(Long crewId, ProfileSummaryDto dto) {
        return new CrewProfileSummaryResult(
                crewId,
                dto.nickname(),
                dto.introduction(),
                dto.avatarBodyUri(),
                dto.combinePositionX(),
                dto.combinePositionY(),
                dto.avatarFaceUri(),
                dto.activitySummaries()
        );
    }
}
