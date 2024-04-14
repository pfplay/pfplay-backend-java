package com.pfplaybackend.api.user.controller.profile.api;

import com.pfplaybackend.api.user.presentation.user.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.user.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserProfileApi {
    @Operation(summary = "유저 마이 프로필 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 설정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "OK"
                            ))
            ),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"이미 존재하는 값입니다.\"}"
                            ))
            ),
            @ApiResponse(responseCode = "500",
                    description = "알 수 없는 에러입니다."
            )
    })
    public ResponseEntity<?> setUserProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    );


    @Operation(summary = "유저 마이 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500",
                    description = "프로필 조회 실패"
            )
    })
    public ResponseEntity<?> getUserProfile();
}
