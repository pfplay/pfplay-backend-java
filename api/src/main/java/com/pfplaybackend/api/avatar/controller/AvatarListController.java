package com.pfplaybackend.api.avatar.controller;

import com.pfplaybackend.api.avatar.service.AvatarService;
import com.pfplaybackend.api.entity.Avatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/api/v1/avatar")
@RestController
public class AvatarListController {
    @Autowired

    AvatarService avatarService;


    @GetMapping("")
    public List<Avatar> getAvatarList() {
        List<Avatar> avatars = avatarService.getAllAvatars();
        return avatars;
    }
}
