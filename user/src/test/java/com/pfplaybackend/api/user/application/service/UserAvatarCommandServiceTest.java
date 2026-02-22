package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAvatarCommandServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock AvatarResourceQueryService avatarResourceQueryService;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks UserAvatarCommandService userAvatarCommandService;

    @Test
    @DisplayName("findAvatarIconByFaceSourceType — INTERNAL_IMAGE이면 pair 아이콘을 반환한다")
    void findAvatarIconByFaceSourceType_internalImage_returnsPairIcon() {
        // given
        AvatarFaceUri faceUri = new AvatarFaceUri("ava_face_01");
        AvatarIconDto iconDto = new AvatarIconDto(1L, "ava_icon_01", "icon_uri_01", true);
        when(avatarResourceQueryService.findPairAvatarIconByFaceUri(faceUri)).thenReturn(iconDto);

        // when
        AvatarIconUri result = userAvatarCommandService.findAvatarIconByFaceSourceType(faceUri, FaceSourceType.INTERNAL_IMAGE);

        // then
        assertThat(result.getAvatarIconUri()).isEqualTo("icon_uri_01");
    }

    @Test
    @DisplayName("findAvatarIconByFaceSourceType — NFT_URI이면 faceUri를 그대로 아이콘으로 사용한다")
    void findAvatarIconByFaceSourceType_nftUri_usesFaceUriAsIcon() {
        // given
        AvatarFaceUri faceUri = new AvatarFaceUri("https://nft.example.com/image.png");

        // when
        AvatarIconUri result = userAvatarCommandService.findAvatarIconByFaceSourceType(faceUri, FaceSourceType.NFT_URI);

        // then
        assertThat(result.getAvatarIconUri()).isEqualTo("https://nft.example.com/image.png");
    }
}
