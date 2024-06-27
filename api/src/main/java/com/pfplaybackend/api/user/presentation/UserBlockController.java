package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserBlockController {

    @GetMapping("/me/block-users")
    public ResponseEntity<?> getBlockUsers()  {
        // TODO 차단 목록 조회
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    @PostMapping("/me/block-users/{uid}")
    public ResponseEntity<?> blockOther(@PathVariable String uid)  {
        // TODO 차단 생성
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    @DeleteMapping("/me/block-users/{uid}")
    public ResponseEntity<?> unblockOther(@PathVariable String uid)  {
        // TODO 차단 해제
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
