package com.pfplaybackend.api.avatarresource.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Avatar Resource API")
@RequestMapping("/api/v1/avatar-resources")
@RestController
@RequiredArgsConstructor
public class AvatarResourceController {


}
