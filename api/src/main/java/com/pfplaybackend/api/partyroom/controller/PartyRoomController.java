package com.pfplaybackend.api.partyroom.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.presentation.request.PartyRoomCreateRequest;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateResponse;
import com.pfplaybackend.api.partyroom.service.PartyRoomService;
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

@Tag(name = "party", description = "party api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/party-room")
public class PartyRoomController {

    private final UserService userService;
    private final PartyRoomService partyRoomService;

    @Operation(summary = "파티룸 생성")
    @ApiResponses(value = {
            @ApiResponse(description = "파티룸 생성",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PartyRoomCreateResponse.class))
            )
    })
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid PartyRoomCreateRequest request) {
        JwtTokenInfo jwtTokenInfo = new JwtTokenInfo(SecurityContextHolder.getContext().getAuthentication());
        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                            .orElseThrow(NoSuchElementException::new);

        PartyRoomCreateDto dto = PartyRoomCreateDto.builder()
                .name(request.getName())
                .user(user)
                .introduce(request.getIntroduce())
                .domain(request.getDomain())
                .limit(request.getLimit())
                .type(PartyRoomType.PARTY)
                .status(PartyRoomStatus.ACTIVE)
                .build();

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(partyRoomService.createPartyRoom(dto)));
    }
}
