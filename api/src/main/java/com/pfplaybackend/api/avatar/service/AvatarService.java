package com.pfplaybackend.api.avatar.service;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.repository.AvatarRepository;
import com.pfplaybackend.api.entity.Avatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public  List<AvatarBodyDto> getAvatarBodys() {
        return avatarRepository.findBy(AvatarBodyDto.class);
    }
}
