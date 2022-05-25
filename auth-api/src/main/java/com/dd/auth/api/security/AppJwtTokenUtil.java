package com.dd.auth.api.security;

import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.model.dto.AuthKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.dd.auth.api.util.AppUtility.JWT_TOKEN_VALIDITY;

@Component
@Slf4j
public class AppJwtTokenUtil {

    private KeyPair keyPair;

    @Value("$secret.key}")
    private String secret;

    @PostConstruct
    public void init() {
        try {
            keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        } catch (Exception ex) {
            log.error("Key upload error", ex);
            throw new ApplicationException("Exception occurred while loading key store! " + ex.getMessage());
        }
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(principal.getUsername());
    }

    public String getUsernameFromToken(String jwt) {
        final Claims claims = getAllClaimsFromToken(jwt);
        return claims.getSubject();
    }

    public boolean validateToken(String jwt) {
        final Claims claims = getAllClaimsFromToken(jwt);
        final Date expiration = claims.getExpiration();
        return !expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String jwt) {
        log.info("JWT String..." + jwt);
        return Jwts.parserBuilder().setSigningKey(keyPair.getPublic())
                .build().parseClaimsJws(jwt).getBody();
    }


    public String generateTokenWithUsername(String username) {
        return doGenerateToken(username);
    }

    private String doGenerateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public AuthKeyResponse getPublicKey() {
        return AuthKeyResponse.builder().publicKey(keyPair.getPublic()).build();
    }
}
