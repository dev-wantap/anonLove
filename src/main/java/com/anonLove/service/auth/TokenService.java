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
    // 액세스 토큰 생성
    public String createAccessToken(Long userId) {
        return tokenProvider.createAccessToken(userId);
    }

    // 리프레시 토큰 생성 및 Redis 저장
    public String createRefreshToken(Long userId) {
        String refreshToken = tokenProvider.createRefreshToken(userId);
        // Redis에 토큰 저장(7일)
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_VALIDITY_DAYS, TimeUnit.DAYS);

        return refreshToken;
    }
    // 리프레시 토큰 유효성 검증
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(key);

        return refreshToken.equals(storedToken) && tokenProvider.validateToken(refreshToken);
    }
    // 리프레시 토큰 삭제
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }
}