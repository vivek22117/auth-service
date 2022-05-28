package com.dd.auth.api.cognito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordRequest {

    private String accessToken;
    private String oldPassword;
    private String password;
}
