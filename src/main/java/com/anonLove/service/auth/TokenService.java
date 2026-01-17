package com.anonLove.service.auth;
import com.anonLove.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh: ";
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    public String createAccessToken(Long userId) {
        return tokenProvider.createAccessToken(userId);
    }

    public String createRefreshToken(Long userId) {
        String refreshToken = tokenProvider.createRefreshToken(userId);

        // Redis에 저장
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_VALIDITY_DAYS, TimeUnit.DAYS);

        return refreshToken;
    }

    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(key);

        return refreshToken.equals(storedToken) && tokenProvider.validateToken(refreshToken);
    }

    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }
}