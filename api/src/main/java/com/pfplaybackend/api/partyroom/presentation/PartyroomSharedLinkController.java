package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomSharedLinkService;
import com.pfplaybackend.api.partyroom.presentation.payload.response.PartyroomSharedLinkResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomSharedLinkController {
    private final PartyroomSharedLinkService partyroomSharedLinkService;

    /**
     * partyroomId를 기준으로 공유 링크를 생성한다.
     * @param partyroomId
     */
    @GetMapping("/shared-link/{partyroomId}")
    public ResponseEntity<PartyroomSharedLinkResponse> getPartyroomSharedLink(
            @PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(partyroomSharedLinkService.getSharedLink(partyroomId));
    }

    /**
     * linkDomain을 통해 Partyroom의 Web 서버의 주소로 redirect 한다.
     * @param linkDomain
     */
    @GetMapping("/shared-link/redirect/{linkDomain}")
    public ResponseEntity<?> redirectToPartyroomWeb(
            @PathVariable String linkDomain) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(partyroomSharedLinkService.getRedirectUri(linkDomain));
        return ResponseEntity.status(MOVED_PERMANENTLY).headers(headers).build();
    }
}
