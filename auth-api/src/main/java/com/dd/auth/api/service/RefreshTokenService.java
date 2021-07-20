package com.dd.auth.api.service;

import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.entity.RefreshToken;
import com.dd.auth.api.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {
    private static final Logger LOGGER = getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(){
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(token);
    }

    public void validateRefreshToken(String token) {
        LOGGER.info("Refresh token received..." + token);
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApplicationException("Invalid refresh token!"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
