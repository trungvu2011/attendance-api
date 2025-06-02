package com.attendance.service;

import com.attendance.entities.RefreshToken;
import com.attendance.entities.User;
import com.attendance.repositories.RefreshTokenRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = new RefreshToken();

        // Xóa token cũ nếu có - using the corrected method name
        refreshTokenRepository.findByUser_UserId(userId).ifPresent(token -> refreshTokenRepository.delete(token));

        // Tạo token mới
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại");
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}