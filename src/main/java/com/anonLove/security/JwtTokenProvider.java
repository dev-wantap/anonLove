package com.anonLove.security;

import com.anonLove.exception.CustomException;
import com.anonLove.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity, // 1시간
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) { // 7일
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }
    // 토큰 생성
    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenValidity);
    }
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenValidity);
    }
    // ID, 유효기간 포함된 토큰 생성
    private String createToken(Long userId, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Token expired");
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Invalid token format");
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Invalid token signature");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Unsupported token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Token claims empty");
        }
    }
    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getUserIdFromToken(token);
            return true;
        } catch (CustomException e) {
            return false;
        }
    }
}