package com.nortal.library.api.controller;

import com.nortal.library.api.config.DevAuthConfig;
import com.nortal.library.api.dto.JWTResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class DevTokenController {

    @GetMapping
    public ResponseEntity<JWTResponse> demoToken() {
        if (DevAuthConfig.DEMO_JWT == null) {
            return ResponseEntity.status(503)
                    .body(new JWTResponse("Token not generated",
                            null));
        }
        return ResponseEntity.ok(
                new JWTResponse("Jwt token",
                        DevAuthConfig.DEMO_JWT)
        );
    }
}
