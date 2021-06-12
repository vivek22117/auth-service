package com.dd.auth.api.controller;

import com.dd.auth.api.entity.ChannelInfo;
import com.dd.auth.api.model.dto.AuthenticationResponse;
import com.dd.auth.api.model.dto.LoginRequest;
import com.dd.auth.api.model.dto.RefreshTokenRequest;
import com.dd.auth.api.model.dto.RegisterRequest;
import com.dd.auth.api.service.AuthService;
import com.dd.auth.api.service.RefreshTokenService;
import com.dd.auth.api.util.AppUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.dd.auth.api.util.AppUtility.*;


@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping(API_AUTH_ROOT_URI)
@Slf4j
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(AUTH_SIGNUP_URI)
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest request) {
        authService.signup(request);
        return new ResponseEntity<>("User Registration Successful!", HttpStatus.OK);
    }

    @GetMapping(ACCOUNT_VERIFICATION_TOKEN_URI)
    public ResponseEntity<String> verifyToken(@PathVariable String token) {
        authService.verityToken(token);
        return new ResponseEntity<>("Account Activated Successfully!", HttpStatus.OK);
    }

    @PostMapping(AUTH_LOGIN_URI)
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping(AUTH_REFRESH_TOKEN_URI)
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping(AUTH_LOGOUT_URI)
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Refresh token deleted successfully!");
    }
}
