package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DjConverter {
    public Dj toDomain(DjData djData) {
        return Dj.builder()
                .id(djData.getId())
                .userId(djData.getUserId())
                .crewId(djData.getCrewId())
                .playlistId(djData.getPlaylistId())
                .orderNumber(djData.getOrderNumber())
                .isDeleted(djData.isDeleted())
                .createdAt(djData.getCreatedAt())
                .updatedAt(djData.getUpdatedAt())
                .build();
    }
    public DjData toData(Dj dj) {
        return DjData.builder()
                .id(dj.getId())
                .userId(dj.getUserId())
                .crewId(dj.getCrewId())
                .playlistId(dj.getPlaylistId())
                .orderNumber(dj.getOrderNumber())
                .isDeleted(dj.isDeleted())
                .createdAt(dj.getCreatedAt())
                .updatedAt(dj.getUpdatedAt())
                .build();
    }
}
