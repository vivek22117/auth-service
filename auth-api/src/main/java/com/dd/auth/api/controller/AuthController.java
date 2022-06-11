package com.dd.auth.api.controller;

import com.dd.auth.api.model.dto.*;
import com.dd.auth.api.service.AuthService;
import com.dd.auth.api.service.PasswordPolicyService;
import com.dd.auth.api.service.RefreshTokenService;
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
    private final PasswordPolicyService passwordPolicyService;


    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService,
                          PasswordPolicyService passwordPolicyService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.passwordPolicyService = passwordPolicyService;
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

    @GetMapping(AUTH_PUBLIC_KEY_URI)
    public ResponseEntity<Map<String, Object>> getPublicKey() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getKey().toJSONObject());
    }

    @PostMapping(path = AUTH_FORGOT_PASSWORD_URI)
    public ResponseEntity<Message> updatePassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        passwordPolicyService.updateResetPasswordToken(forgotPasswordRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Message.builder()
                        .message("Please check your email inbox for password reset instructions.")
                        .status("Success")
                        .build());
    }

    @PostMapping(path = AUTH_RESET_PASSWORD_URI)
    public ResponseEntity<Message> processResetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        passwordPolicyService.getByResetPasswordToken(resetPasswordRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Message.builder()
                        .message("You've successfully reset your password.")
                        .status("Success")
                        .build());
    }
}
