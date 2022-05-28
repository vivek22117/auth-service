package com.dd.auth.api.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtConfiguration {

    @Value(value = "${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value(value = "${aws.cognito.clientId}")
    private String clientId;

    private String jwkUrl;

    @Value("${awsRegion: No value}")
    private String region;

    private final int connectionTimeout = 2000;
    private final int readTimeout = 2000;
    private final String httpHeader = "Authorization";


    public String getJwkUrl() {
        return this.jwkUrl != null && !this.jwkUrl.isEmpty() ? this.jwkUrl : String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", this.region, this.userPoolId);
    }

    public String getCognitoIdentityPoolUrl() {
        return String.format("https://cognito-idp.%s.amazonaws.com/%s", this.region, this.userPoolId);
    }
}
