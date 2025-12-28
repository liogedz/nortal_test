package com.nortal.library.api.controller;

import com.nortal.library.api.config.DevAuthConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class DevTokenController {

    @GetMapping
    public String demoToken() {
        return DevAuthConfig.DEMO_JWT;
    }
}
