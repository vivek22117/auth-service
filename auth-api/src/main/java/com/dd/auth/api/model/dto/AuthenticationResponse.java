package com.dd.auth.api.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class AuthenticationResponse {
    private String authenticationToken;
    private String idToken;
    private String refreshToken;
    private String tokenType;
    private Instant expiresAt;
    private String username;
}
