package com.pfplaybackend.api.playlist.service;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.enums.PlayListOrder;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayListService {
    private PlayListRepository playListRepository;

    public PlayListService(PlayListRepository playListRepository) {
        this.playListRepository = playListRepository;
    }

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

    public List<PlayListDto> getPlayList(User user, PlayListType type, PlayListOrder order) {
        PlayListType playListType = type != null ? type : PlayListType.valueOf("PLAYLIST");
        PlayListOrder playListOrder = order != null ? order : PlayListOrder.valueOf("DESC");
        List<PlayList> result = playListOrder == PlayListOrder.DESC
                ? playListRepository.findByUserIdAndTypeOrderByOrderNumberDesc(user.getId(), playListType)
                : playListRepository.findByUserIdAndTypeOrderByOrderNumberAsc(user.getId(), playListType);

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
