package com.pfplaybackend.api.avatar.service;

import com.pfplaybackend.api.avatar.enums.AvatarType;
import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.repository.AvatarRepository;
import com.pfplaybackend.api.entity.Avatar;
import com.pfplaybackend.api.pointhistory.presentation.dto.PointHistoryGroupByDto;
import com.pfplaybackend.api.pointhistory.service.PointHistoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;

    private PointHistoryService pointHistoryService;

    public AvatarService(AvatarRepository avatarRepository, PointHistoryService pointHistoryService) {
        this.avatarRepository = avatarRepository;
        this.pointHistoryService = pointHistoryService;
    }

    public List<AvatarBodyDto> getAvatarBodies(Long userId) {
        List<Avatar> result = avatarRepository.findAll();
        List<AvatarBodyDto> dtoList = new ArrayList<>();

        List<PointHistoryGroupByDto> pointHistory = pointHistoryService.getPointHistory(userId);

        for (Avatar avatar : result) {
            Boolean isAvailable = false;
            Long myPoint = 0L;
            for (PointHistoryGroupByDto pointHistoryDto : pointHistory) {
                if (avatar.getType().equals(pointHistoryDto.getType())) {
                    if (avatar.getPoint() <= pointHistoryDto.getPoint()) {
                        isAvailable = true;
                    }
                    myPoint = pointHistoryDto.getPoint();
                }
            }

            if (avatar.getType().equals(AvatarType.BASIC)) {
                isAvailable = true;
            }

            AvatarBodyDto dto = AvatarBodyDto.builder()
                    .id(avatar.getId())
                    .type(avatar.getType())
                    .name(avatar.getName())
                    .image(avatar.getImage())
                    .myPoint(myPoint)
                    .requiredPoint(avatar.getPoint())
                    .isAvailable(isAvailable)
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
}
