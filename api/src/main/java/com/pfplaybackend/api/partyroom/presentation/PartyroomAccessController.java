package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PartyroomAccessService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.response.EnterPartyroomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessController {

    private final PartyroomAccessService partyroomAccessService;

    /**
     *
     * @param partyroomId
     * @return
     */
    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<?> enterPartyroom(
            @PathVariable Long partyroomId) {
        Partymember partymember = partyroomAccessService.tryEnter(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(EnterPartyroomResponse.from(partymember)));
    }

    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<?> exitPartyroom(
            @PathVariable Long partyroomId) {
        partyroomAccessService.exit(new PartyroomId(partyroomId));
        return ResponseEntity.ok().build();
    }

    // TODO Guest 인증 절차 없이 접근한 경우, 어떻게 처리해야 하는가?
    @PostMapping("/link/{linkAddress}/enter")
    public ResponseEntity<?> enterPartyroomByLinkAddress(
            @PathVariable String linkAddress) {
        // TODO Check: Has Client JWT Token?
        // 없다면 '게스트 추가' 후 쿠키 설정
        // 응답 객체는 linkAddress 를 partyroomId로 Resolve 한 결과를 리턴한다.
        return ResponseEntity.ok().build();
    }
}
