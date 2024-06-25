package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.dj.DJQueueManagementService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.DJQueueAddRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 특정 파티룸 내에서의 DJ 관리에 대한 표현 계층을 담당한다.
 */
@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DJQueueManagementController {

    private final DJQueueManagementService djQueueManagementService;

    @PutMapping("/{partyroomId}/dj/queue")
    public void lockQueue(@PathVariable Long partyroomId) {
        djQueueManagementService.lockQueue();
    }

    /**
     * 자신을 대기열에 등록한다.
     * @param partyroomId
     * @param djQueueAddRequest
     */
    @PostMapping("/{partyroomId}/dj/queue/djs")
    public void addDJToQueue(@PathVariable Long partyroomId,
                             @RequestBody DJQueueAddRequest djQueueAddRequest) {
        djQueueManagementService.addDJToQueue();
    }

    /**
     *
     * @param partyroomId
     */
    @DeleteMapping("/{partyroomId}/dj/queue/djs/me")
    public void removeDJFromQueue(@PathVariable Long partyroomId) {
        djQueueManagementService.removeDJFromQueue();
    }

    /**
     * 파티룸 운영진에 의해 특정인이 제외될 수 있다.
     * @param partyroomId
     * @param djId
     */
    @DeleteMapping("/{partyroomId}/dj/queue/djs/{djId}")
    public void removeDJFromQueue(@PathVariable Long partyroomId,
                                  @PathVariable String djId) {
        djQueueManagementService.removeDJFromQueue();
    }
}