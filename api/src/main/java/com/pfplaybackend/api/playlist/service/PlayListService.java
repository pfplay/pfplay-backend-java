package com.pfplaybackend.api.playlist.service;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlayListService {
    private PlayListRepository playListRepository;

    public PlayListService(PlayListRepository playListRepository) {
        this.playListRepository = playListRepository;
    }

    @Transactional
    public PlayList createPlayList(PlayListCreateRequest request, User user) {
        PlayListCreateDto dto;
        Optional<PlayList> result = playListRepository.findTopByUserIdOrderByOrderNumberDesc(user.getId());

        if (result.isPresent()) {
            PlayList playList = result.get();
            dto = PlayListCreateDto.builder()
                    .orderNumber(playList.getOrderNumber() + 1)
                    .user(user)
                    .name(request.getName())
                    .type(PlayListType.valueOf(request.getType()))
                    .build();
        } else {
            dto = PlayListCreateDto.builder()
                    .orderNumber(1L)
                    .user(user)
                    .name(request.getName())
                    .type(PlayListType.PLAYLIST)
                    .build();
        }

        return playListRepository.save(dto.toEntity());
    }
}
