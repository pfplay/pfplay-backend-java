package com.pfplaybackend.api.partyroom.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.common.util.CustomStringUtils;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomJoinResultDto;
import com.pfplaybackend.api.partyroom.presentation.request.PartyRoomCreateRequest;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateResponse;
import com.pfplaybackend.api.partyroom.service.PartyRoomService;
import com.pfplaybackend.api.user.service.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "party", description = "party api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/party-room")
public class PartyRoomController {

    private final PartyRoomService partyRoomService;
    private final CustomUserDetailService customUserDetailService;

    @Operation(summary = "파티룸 생성")
    @ApiResponses(value = {
            @ApiResponse(description = "파티룸 생성",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PartyRoomCreateResponse.class))
            )
    })
    @Secured({ "ROLE_USER", "ROLE_WALLET_USER" })
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid PartyRoomCreateRequest request) {
        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());

        String domain = request.getDomain();
        if(request.isDomainOption()) {
            domain = CustomStringUtils.base64Encoder(CustomStringUtils.getRandomUuidWithoutHyphen().substring(0, 10));
        }

        PartyRoomCreateDto dto = PartyRoomCreateDto.builder()
                .name(request.getName())
                .user(jwtTokenInfo.getUser())
                .introduce(request.getIntroduce())
                .domain(domain)
                .limit(request.getLimit())
                .type(PartyRoomType.PARTY)
                .status(PartyRoomStatus.ACTIVE)
                .domainOption(request.isDomainOption())
                .build();

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(partyRoomService.createPartyRoom(dto)));
    }

    @GetMapping("/join")
    @Operation(summary = "파티룸 입장")
    @ApiResponses(value = {
            @ApiResponse(description = "파티룸 입장",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PartyRoomJoinResultDto.class))
            )
    })
    public ResponseEntity<?> join(@RequestParam Long id) {
        JwtTokenInfo jwtTokenInfo =
                customUserDetailService.getUserDetails(
                        SecurityContextHolder.getContext().getAuthentication()
                );
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(partyRoomService.join(id, jwtTokenInfo)));
    }
}
