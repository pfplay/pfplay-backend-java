package com.pfplaybackend.api.user.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/member")
public class MemberSignController {
    @GetMapping("/sign")
    public String memberSign(@RequestParam("redirect_location") String redirectLocation) {
        return "redirect:http://localhost:8080/oauth2/authorization/google?redirect_location=" + redirectLocation;
    }
}