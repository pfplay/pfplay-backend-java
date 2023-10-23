package com.pfplaybackend.api.avatar.service;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.repository.AvatarRepository;
import com.pfplaybackend.api.entity.Avatar;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public List<AvatarBodyDto> getAvatarBodies() {
        List<Avatar> result = avatarRepository.findAll();
        List<AvatarBodyDto> dtoList = new ArrayList<>();
        for (Avatar avatar : result) {
            AvatarBodyDto dto = AvatarBodyDto.builder()
                    .id(avatar.getId())
                    .type(avatar.getType())
                    .name(avatar.getName())
                    .image(avatar.getImage())
                    .point(avatar.getPoint())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
