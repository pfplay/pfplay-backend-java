package com.pfplaybackend.api.user.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User add API")
@Profile("local")
@RequestMapping("/api/v1/users")
@RestController
public class TemporaryUserController {

    @GetMapping("/test")
    public void addUsers() {

    }
}
