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
                .playlistId(djData.getPlaylistId())
                .orderNumber(djData.getOrderNumber())
                .isDeleted(djData.isDeleted())
                .build();
    }
    public DjData toData(Dj dj) {
        return DjData.builder()
                .id(dj.getId())
                .userId(dj.getUserId())
                .playlistId(dj.getPlaylistId())
                .orderNumber(dj.getOrderNumber())
                .isDeleted(dj.isDeleted())
                .build();
    }
}
