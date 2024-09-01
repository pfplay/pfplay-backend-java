package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PartyroomAccessService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.response.EnterPartyroomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;


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

    @GetMapping("/link/{linkDomain}/enter")
    public ResponseEntity<?> enterPartyroomByLinkAddress(
            @PathVariable String linkDomain) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(partyroomAccessService.getRedirectUri(linkDomain));
        return ResponseEntity.status(MOVED_PERMANENTLY).headers(headers).build();
    }
}
