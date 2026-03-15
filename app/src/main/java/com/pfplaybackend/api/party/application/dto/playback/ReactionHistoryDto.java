package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "재생 리액션 히스토리")
public record ReactionHistoryDto(
        @Schema(description = "좋아요 여부", example = "false") boolean isLiked,
        @Schema(description = "싫어요 여부", example = "false") boolean isDisliked,
        @Schema(description = "그랩 여부", example = "false") boolean isGrabbed
) {
    public static ReactionHistoryDto empty() {
        return new ReactionHistoryDto(false, false, false);
    }

    public static ReactionHistoryDto from(ReactionState state) {
        return new ReactionHistoryDto(state.liked(), state.disliked(), state.grabbed());
    }

    public static ReactionHistoryDto from(PlaybackReactionHistoryData data) {
        return new ReactionHistoryDto(data.isLiked(), data.isDisliked(), data.isGrabbed());
    }
}
