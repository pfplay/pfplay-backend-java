package com.pfplaybackend.api.partyroom.application.dto.partyroom;

import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DjDataDto {
    private Long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private PlaylistId playlistId;
    private Integer orderNumber;

    public static DjDataDto from(DjData djData) {
        return new DjDataDto(
                djData.getId(),
                djData.getPartyroomData().getPartyroomId(),
                djData.getUserId(),
                djData.getPlaylistId(),
                djData.getOrderNumber()
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
