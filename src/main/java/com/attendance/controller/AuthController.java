package com.attendance.controller;

import com.attendance.dto.auth.*;
import com.attendance.entities.RefreshToken;
import com.attendance.entities.User;
import com.attendance.security.jwt.JwtUtils;
import com.attendance.security.service.UserDetailsImpl;
import com.attendance.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    
    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;
    
    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenDurationMs;
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO, 
                                             HttpServletResponse response) {
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
            
            // Lưu JWT token vào cookie
            Cookie jwtCookie = new Cookie("access_token", jwt);
            jwtCookie.setMaxAge(jwtExpirationMs / 1000); // convert từ milliseconds sang seconds
            jwtCookie.setHttpOnly(true); // Không cho phép JavaScript truy cập để tăng bảo mật
            jwtCookie.setPath("/"); // Cookie có hiệu lực trên toàn bộ domain
            response.addCookie(jwtCookie);
            
            // Lưu refresh token vào cookie
            Cookie refreshCookie = new Cookie("refresh_token", refreshToken.getToken());
            refreshCookie.setMaxAge((int)(refreshTokenDurationMs / 1000)); // convert từ milliseconds sang seconds
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);
            
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
                                return role.equals("CANDIDATE") ?
                                        User.Role.CANDIDATE :
                                        User.Role.ADMIN;
                            })
                            .orElse(User.Role.CANDIDATE)
            );
            
            // Thêm hướng dẫn sử dụng token trong response
            Map<String, Object> response_data = new HashMap<>();
            response_data.put("authentication", jwtResponse);
            response_data.put("tokenType", "Bearer");
            response_data.put("expiresIn", jwtExpirationMs / 1000); // Chuyển đổi từ milliseconds sang seconds
            response_data.put("refreshToken", refreshToken.getToken());
            response_data.put("message", "Đăng nhập thành công. Token đã được lưu vào cookie.");
            
            return ResponseEntity.ok(response_data);
        } catch (Exception e) {
            logger.error("Authentication failed for email: " + loginDTO.getEmail(), e);
            throw e;
        }
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam(value = "refreshToken", required = false) String paramRefreshToken, 
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        // Biến để lưu token cuối cùng
        final String finalRefreshToken;
        
        // Nếu không có refreshToken trong request param, tìm trong cookie
        if (paramRefreshToken == null || paramRefreshToken.isEmpty()) {
            String cookieToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refresh_token".equals(cookie.getName())) {
                        cookieToken = cookie.getValue();
                        break;
                    }
                }
            }
            finalRefreshToken = cookieToken; // Gán giá trị từ cookie hoặc null
        } else {
            finalRefreshToken = paramRefreshToken; // Sử dụng giá trị từ param
        }
        
        if (finalRefreshToken == null || finalRefreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Refresh token không được tìm thấy"));
        }
        
        return refreshTokenService.findByToken(finalRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    
                    // Lưu access token mới vào cookie
                    Cookie jwtCookie = new Cookie("access_token", token);
                    jwtCookie.setMaxAge(jwtExpirationMs / 1000);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);
                    
                    return ResponseEntity.ok(new TokenRefreshResponse(
                            token,
                            finalRefreshToken,
                            "Bearer",
                            jwtExpirationMs / 1000
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống"));
    }
    
    @PostMapping("/signout")
    public ResponseEntity<Map<String, String>> signout(HttpServletResponse response) {
        logger.info("Signout endpoint called");
        
        try {
            // Xóa cookies bất kể trạng thái đăng nhập
            Cookie jwtCookie = new Cookie("access_token", "");
            jwtCookie.setMaxAge(0);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
            
            Cookie refreshCookie = new Cookie("refresh_token", "");
            refreshCookie.setMaxAge(0);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);
            
            // Xóa thông tin xác thực trong context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                logger.info("Logging out user: {}", userDetails.getUsername());
                refreshTokenService.deleteByUserId(userDetails.getId());
            }
            
            // Xóa SecurityContext
            SecurityContextHolder.clearContext();
            
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Đăng xuất thành công");
            responseMap.put("status", "success");
            
            logger.info("Logout completed successfully");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage(), e);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Đăng xuất thành công mặc dù có lỗi");
            responseMap.put("status", "success");
            return ResponseEntity.ok(responseMap);
        }
    }
    
    @RequestMapping(path = "/logout", method = RequestMethod.POST, produces = "application/json")
    public Map<String, String> logout(HttpServletResponse response) {
        logger.info("Logout endpoint called");
        
        try {
            // Xóa cookies
            Cookie jwtCookie = new Cookie("access_token", "");
            jwtCookie.setMaxAge(0);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
            
            Cookie refreshCookie = new Cookie("refresh_token", "");
            refreshCookie.setMaxAge(0);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);
            
            // Xóa context
            SecurityContextHolder.clearContext();
            
            logger.info("Logout successful via /logout endpoint");
        } catch (Exception e) {
            logger.error("Error in /logout endpoint: {}", e.getMessage(), e);
        }
        
        // Trả về Map trực tiếp, không dùng ResponseEntity
        Map<String, String> response_data = new HashMap<>();
        response_data.put("message", "Đăng xuất thành công");
        response_data.put("status", "success");
        return response_data;
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         HttpServletRequest request) {
        try {
            String token = null;
            
            // Lấy token từ header nếu có
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } 
            // Nếu không có trong header, tìm trong cookie
            else {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("access_token".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            
            if (token != null && jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                return ResponseEntity.ok(Map.of("valid", true, "username", username));
            }
            
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Token không hợp lệ hoặc không tìm thấy"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
}