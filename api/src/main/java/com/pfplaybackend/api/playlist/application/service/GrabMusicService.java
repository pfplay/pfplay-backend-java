package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.exception.TrackException;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddMusicRequest;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrabMusicService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository playlistMusicRepository;
    private final MusicCommandService musicCommandService;

    @Transactional
    public void grabMusic(UserId userId, String linkId) {
        // Get 'PlaylistMusic' Record By linkId
        PlaylistMusicData targetPlaylistMusicData = playlistMusicRepository.findFirstByLinkId(linkId);

        PlaylistData playlistData = playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST);
        // LinkId cannot be duplicated.
        Optional<PlaylistMusicData> optional = playlistMusicRepository.findByPlaylistDataIdAndLinkId(playlistData.getId(), linkId);
        if(optional.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);

        AddMusicRequest request = new AddMusicRequest(
                targetPlaylistMusicData.getName(),
                targetPlaylistMusicData.getLinkId(),
                targetPlaylistMusicData.getDuration(),
                targetPlaylistMusicData.getThumbnailImage()
        );
        musicCommandService.addMusicInPlaylist(playlistData.getId(), request);
    }
}
