package com.pfplaybackend.api.partyroom.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "파티룸 홈화면")
public class PartyRoomHomeResultDto {
    @Schema(description = "파티룸 소개")
    private String introduce;
    @Schema(description = "생성일")
    private LocalDateTime createdAt;
    @Schema(description = "파티룸 참여 총 수")
    private Long participantTotalCount;
    @Schema(description = "파티룸 참여자 리스트. ADMIN > CM > MOD 순으로 최대 3명 정렬")
    private List<Participant> participants;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Participant {
        private String nickname;
        private String faceUrl;
    }

}

