package com.pfplaybackend.api.avatar.service;

import com.pfplaybackend.api.avatar.repository.AvatarRepository;
import com.pfplaybackend.api.entity.Avatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvatarService {
    @Autowired
    AvatarRepository avatarRepository;


    public List<Avatar> getAllAvatars() {
        return avatarRepository.findAll();
    }
}
