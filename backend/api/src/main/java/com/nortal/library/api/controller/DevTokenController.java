package com.nortal.library.api.controller;

import com.nortal.library.api.config.DevAuthConfig;
import com.nortal.library.api.dto.JwtResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class DevTokenController {

  @GetMapping
  public ResponseEntity<JwtResponse> demoToken() {
    if (DevAuthConfig.DEMO_JWT == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new JwtResponse("Token not generated", null));
    }
    return ResponseEntity.ok(new JwtResponse("Jwt token", DevAuthConfig.DEMO_JWT));
  }
}
