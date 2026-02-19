package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.domain.exception.TrackException;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrabMusicService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final TrackCommandService trackCommandService;

    @Transactional
    public void grabMusic(UserId userId, String linkId) {
        // Get 'PlaylistMusic' Record By linkId
        TrackData targetTrackData = trackRepository.findFirstByLinkId(linkId);

        PlaylistData playlistData = playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST);
        // LinkId cannot be duplicated.
        Optional<TrackData> optional = trackRepository.findByPlaylistDataIdAndLinkId(playlistData.getId(), linkId);
        if(optional.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);

        AddTrackRequest request = new AddTrackRequest(
                targetTrackData.getName(),
                targetTrackData.getLinkId(),
                targetTrackData.getDuration(),
                targetTrackData.getThumbnailImage()
        );
        trackCommandService.addTrackInPlaylist(playlistData.getId(), request);
    }
}
