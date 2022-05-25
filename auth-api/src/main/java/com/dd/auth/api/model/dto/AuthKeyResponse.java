package com.dd.auth.api.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class AuthKeyResponse {

    private PublicKey publicKey;
}
