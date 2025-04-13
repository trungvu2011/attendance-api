package com.attendance.controller;

import com.attendance.dto.auth.*;
import com.attendance.entities.RefreshToken;
import com.attendance.security.jwt.JwtUtils;
import com.attendance.security.service.UserDetailsImpl;
import com.attendance.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            logger.info("Login attempt for email: {}", loginDTO.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Tạo access token
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Tạo refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            
            logger.info("User successfully authenticated: {}", userDetails.getUsername());
            
            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream()
                            .findFirst()
                            .map(authority -> {
                                String role = authority.getAuthority();
                                if (role.startsWith("ROLE_")) {
                                    role = role.substring(5);
                                }
                                return role.equals("STUDENT") ? 
                                        com.attendance.entities.User.Role.STUDENT : 
                                        com.attendance.entities.User.Role.TEACHER;
                            })
                            .orElse(com.attendance.entities.User.Role.STUDENT)
            );
            
            // Thêm hướng dẫn sử dụng token trong response
            Map<String, Object> response = new HashMap<>();
            response.put("authentication", jwtResponse);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtUtils.getJwtExpirationMs() / 1000); // Chuyển đổi từ milliseconds sang seconds
            response.put("refreshToken", refreshToken.getToken());
            response.put("usage", "Để sử dụng token, thêm header 'Authorization: Bearer " + jwt + "' vào mỗi request");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Authentication failed for email: " + loginDTO.getEmail(), e);
            throw e;
        }
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    
                    return ResponseEntity.ok(new TokenRefreshResponse(
                            token,
                            requestRefreshToken,
                            "Bearer",
                            jwtUtils.getJwtExpirationMs() / 1000
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getId());
        
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }
    
    // Endpoint để kiểm tra token có hợp lệ không
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Trích xuất token từ header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = jwtUtils.validateJwtToken(token);
                
                if (isValid) {
                    String username = jwtUtils.getUserNameFromJwtToken(token);
                    return ResponseEntity.ok(Map.of("valid", true, "username", username));
                }
            }
            
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Token không hợp lệ"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
}