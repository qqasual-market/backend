package org.example.market.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.market.requests.JwtAuthenticationResponse;
import org.example.market.requests.Register;
import org.example.market.requests.SignInRequest;
import org.example.market.response.TokenResponse;
import org.example.market.services.AuthorizationService;
import org.example.market.services.JwtTokensService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthorizationService authorizationService;
    private final JwtTokensService jwtTokensService;

    @PostMapping("/registration")
    public ResponseEntity<Void> signUp(@Valid @RequestBody Register register) {
        authorizationService.registrationUser(register);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtTokens = authorizationService.signIn(signInRequest);
        return ResponseEntity.ok()
                .header("AccessToken", jwtTokens.getAccessToken())
                .header("RefreshToken", jwtTokens.getRefreshToken())
                .build();
    }

}
