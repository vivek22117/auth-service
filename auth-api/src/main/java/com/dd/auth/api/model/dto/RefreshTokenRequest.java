package com.dd.auth.api.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
    private String username;
}
