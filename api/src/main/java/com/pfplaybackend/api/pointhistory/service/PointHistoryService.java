package com.pfplaybackend.api.pointhistory.service;

import com.pfplaybackend.api.pointhistory.enums.PointType;
import com.pfplaybackend.api.pointhistory.presentation.dto.PointHistoryGroupByDto;
import com.pfplaybackend.api.pointhistory.repository.PointHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PointHistoryService {
    private PointHistoryRepository pointHistoryRepository;

    public PointHistoryService(PointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public List<PointHistoryGroupByDto> getPointHistory(Long userId) {
        List<List<Object>> result = pointHistoryRepository.getPointHistoryGroupBy(userId);

        List<PointHistoryGroupByDto> dtoList = new ArrayList<>();
        for (List<Object> list : result) {
            PointHistoryGroupByDto dto = PointHistoryGroupByDto.builder()
                    .type(PointType.valueOf((String) list.get(0)))
                    .point((Long) list.get(1))
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
}