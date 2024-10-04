package com.pfplaybackend.api.partyroom.application.dto.base;

import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DjDataDto {
    private Long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private CrewId crewId;
    private PlaylistId playlistId;
    private Integer orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DjData toData() {
        return DjData.builder()
                .id(this.id)
                .userId(this.userId)
                .crewId(this.crewId)
                .playlistId(this.playlistId)
                .orderNumber(this.orderNumber)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public static DjDataDto from(DjData djData) {
        return new DjDataDto(
                djData.getId(),
                djData.getPartyroomData().getPartyroomId(),
                djData.getUserId(),
                djData.getCrewId(),
                djData.getPlaylistId(),
                djData.getOrderNumber(),
                djData.getCreatedAt(),
                djData.getUpdatedAt()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DjDataDto that = (DjDataDto) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
