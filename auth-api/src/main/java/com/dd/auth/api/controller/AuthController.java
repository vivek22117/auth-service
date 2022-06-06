package com.dd.auth.api.controller;

import com.dd.auth.api.cognito.PasswordRequest;
import com.dd.auth.api.cognito.UserResponse;
import com.dd.auth.api.model.dto.*;
import com.dd.auth.api.service.AuthService;
import com.dd.auth.api.service.CognitoAuthService;
import com.dd.auth.api.service.RefreshTokenService;
import com.dd.auth.api.util.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Map;

import static com.dd.auth.api.util.AppUtility.*;


@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping(API_AUTH_ROOT_URI)
@Slf4j
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CognitoAuthService cognitoAuthService;


    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService,
                          CognitoAuthService cognitoAuthService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.cognitoAuthService = cognitoAuthService;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(AUTH_SIGNUP_URI)
    @Timed(value = "auth.signup", description = "Time taken to signUp")
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest request) {
         authService.signup(request);
//        cognitoAuthService.cognitoUserSignUp(request);
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

    @PostMapping(AUTH_CHANGE_PASSWORD_URI)
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        cognitoAuthService.changePassword(passwordRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }

    @GetMapping(AUTH_PUBLIC_KEY_URI)
    public ResponseEntity<Map<String, Object>> getPublicKey() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getKey().toJSONObject());
    }
}
