package com.pfplaybackend.api.party.domain.port;

import com.pfplaybackend.api.party.domain.entity.data.DjData;

import java.util.List;

public interface DjLoadPort {
    List<DjData> findByPartyroomIdOrdered(Long partyroomId);
    boolean existsByPartyroomId(Long partyroomId);
    void removeAll(List<DjData> djs);
    void saveAll(List<DjData> djs);
}
