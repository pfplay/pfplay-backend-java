package com.pfplaybackend.api.playlist.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.partyroom.service.PartyRoomService;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.response.PlayListCreateResponse;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

@Tag(name = "playlist", description = "playlist api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/play-list")
public class PlayListController {
    private final UserService userService;
    private final PartyRoomService partyRoomService;

    @Operation(summary = "플레이리스트 생성")
    @ApiResponses(value = {
            @ApiResponse(description = "플레이리스트 생성",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PlayListCreateResponse.class))
            )
    })
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlayListCreateRequest request) {
        JwtTokenInfo jwtTokenInfo = new JwtTokenInfo(SecurityContextHolder.getContext().getAuthentication());
        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                            .orElseThrow(NoSuchElementException::new);

//        PlayListCreateDto dto = PlayListCreateDto.builder()
//                .name(request.getName())
//                .user(user)
//                .introduce(request.getIntroduce())
//                .domain(request.getDomain())
//                .limit(request.getLimit())
//                .type(PartyRoomType.PARTY)
//                .status(PartyRoomStatus.ACTIVE)
//                .build();

//        return ResponseEntity
//                .ok()
//                .body(ApiCommonResponse.success(
//                    PlayListCreateResponse.toResponse(partyRoomService.createPartyRoom(dto), user))
//                );
        return null;
    }
}
