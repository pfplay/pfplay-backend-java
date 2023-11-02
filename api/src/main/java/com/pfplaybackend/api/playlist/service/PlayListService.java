package com.pfplaybackend.api.playlist.service;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayListService {
    private PlayListRepository playListRepository;

    public PlayListService(PlayListRepository playListRepository) {
        this.playListRepository = playListRepository;
    }

    public PlayList createPlayList(PlayListCreateRequest request, User user) {
        List<PlayList> result = playListRepository.findByUserIdAndTypeOrderByOrderNumberDesc(user.getId(), PlayListType.PLAYLIST);
        if (result.size() > 0 && user.getWalletAddress() == null) {
            throw new RuntimeException("생성 개수 제한 초과 (지갑 미연동)");
        }
        if (result.size() > 9 && user.getWalletAddress() != null) {
            throw new RuntimeException("생성 개수 제한 초과");
        }

        PlayListCreateDto dto;
        if (!result.isEmpty()) {
            dto = PlayListCreateDto.builder()
                    .orderNumber(result.get(0).getOrderNumber() + 1)
                    .user(user)
                    .name(request.getName())
                    .type(PlayListType.PLAYLIST)
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

    public List<PlayListDto> getPlayList(User user) {
        List<PlayList> result = playListRepository.findByUserIdOrderByTypeDescOrderNumberAsc(user.getId());
        List<PlayListDto> dtoList = new ArrayList<>();
        for (PlayList playList : result) {
            PlayListDto dto = PlayListDto.builder()
                    .id(playList.getId())
                    .name(playList.getName())
                    .orderNumber(playList.getOrderNumber())
                    .type(playList.getType()).build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
