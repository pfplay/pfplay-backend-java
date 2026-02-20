package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.port.DjLoadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DjLoadAdapter implements DjLoadPort {

    private final DjRepository djRepository;

    @Override
    public List<DjData> findByPartyroomIdOrdered(Long partyroomId) {
        return djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId);
    }

    @Override
    public boolean existsByPartyroomId(Long partyroomId) {
        return djRepository.existsByPartyroomDataId(partyroomId);
    }

    @Override
    public void removeAll(List<DjData> djs) {
        djRepository.deleteAll(djs);
    }

    @Override
    public void saveAll(List<DjData> djs) {
        djRepository.saveAll(djs);
    }
}
