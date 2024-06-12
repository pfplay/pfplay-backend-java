package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.presentation.payload.request.DJQueueAddRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 이 클래스는 특정 파티룸 내에서의 DJ 관리에 대한 표현 계층을 담당한다.
 */
@Tag(name = "DJ Queue Management API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DJQueueManagementController {

    @PutMapping("/{partyroomId}/dj/queue")
    public void lockQueue(@PathVariable Long partyroomId) {
    }

    @PostMapping("/{partyroomId}/dj/queue/djs")
    public void addDJToQueue(@PathVariable Long partyroomId,
                             @RequestBody DJQueueAddRequest DJQueueAddRequest) {
    }

    @DeleteMapping("/{partyroomId}/dj/queue/djs/{uid}")
    public void removeDJFromQueue(@PathVariable Long partyroomId,
                                  @PathVariable String uid) {

    }
}