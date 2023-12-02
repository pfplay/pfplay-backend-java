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

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;

    private PointHistoryService pointHistoryService;

    public AvatarService(AvatarRepository avatarRepository, PointHistoryService pointHistoryService) {
        this.avatarRepository = avatarRepository;
        this.pointHistoryService = pointHistoryService;
    }

    public AvatarBodyDto getAvatarBody(Integer bodyId) {
        Avatar avatar = avatarRepository.findById(bodyId).orElseThrow();
        AvatarBodyDto dto = AvatarBodyDto.builder()
                .id(avatar.getId())
                .type(avatar.getType())
                .name(avatar.getName())
                .image(avatar.getImage())
                .requiredPoint(avatar.getPoint())
                .isUniform(avatar.getIsUniform())
                .build();
        return dto;
    }

    public List<AvatarBodyDto> getAvatarBodies(Long userId) {
        List<Avatar> result = avatarRepository.findAll();
        List<AvatarBodyDto> dtoList = new ArrayList<>();

        List<PointHistoryGroupByDto> pointHistory = pointHistoryService.getPointHistory(userId);
        for (Avatar avatar : result) {
            boolean isAvailable = false;
            Long myPoint = 0L;
            for (PointHistoryGroupByDto pointHistoryDto : pointHistory) {
                if (avatar.getType().name().equals(pointHistoryDto.getType().name())) {
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
                    .isUniform(avatar.getIsUniform())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
}
