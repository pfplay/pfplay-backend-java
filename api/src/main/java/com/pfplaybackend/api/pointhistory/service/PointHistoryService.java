package com.pfplaybackend.api.pointhistory.service;

import com.pfplaybackend.api.pointhistory.presentation.dto.PointHistoryGroupByDto;
import com.pfplaybackend.api.pointhistory.repository.PointHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PointHistoryService {
    private PointHistoryRepository pointHistoryRepository;

    public PointHistoryService(PointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public List<PointHistoryGroupByDto> getPointHistory(Long userId) {
        List<PointHistoryGroupByDto> result = pointHistoryRepository.getPointHistoriesByUserIdGroupByPointType(userId);
        return result;
    }
}