package com.pfplaybackend.api.user.controller.user.api;

import com.pfplaybackend.api.user.presentation.user.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.user.request.TokenRequest;
import com.pfplaybackend.api.user.presentation.user.response.UserLoginSuccessResponse;
import com.pfplaybackend.api.user.presentation.user.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "user", description = "user api")
public interface UserApi {

    @Operation(summary = "유저 회원가입 및 로그인")
    @ApiResponses(value = {
            @ApiResponse(description = "유저 회원가입 및 로그인",
                    content = @Content(
                            schema = @Schema(implementation = UserLoginSuccessResponse.class)
                    )
            )
    })
    public ResponseEntity<?> userInfo(
            @RequestBody TokenRequest request
    );


    @ApiResponses(value = {
            @ApiResponse(description = "jwt 유저 인증 테스트. 게스트 jwt 접근 불가능",
                    content = @Content(
                            schema = @Schema(implementation = UserLoginSuccessResponse.class)
                    )
            )
    })
    public ResponseEntity<?> dummy();

}
