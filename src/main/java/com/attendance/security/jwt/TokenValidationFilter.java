package com.attendance.security.jwt;

import com.attendance.dto.auth.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class TokenValidationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenValidationFilter.class);
    
    // Danh sách các URL public không cần token
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/auth/login",
            "/api/user/register",
            "/error"
    );

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Cho phép các URL public không cần xác thực
        if (isPublicUrl(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = getTokenFromRequest(request);
            
            // Kiểm tra nếu không có token
            if (token == null) {
                logger.error("Token missing from both Authorization header and cookies");
                sendErrorResponse(response, "Không tìm thấy token xác thực");
                return;
            }
            
            // Validate token
            if (!jwtUtils.validateJwtToken(token)) {
                logger.error("Token validation failed");
                sendErrorResponse(response, "Token không hợp lệ hoặc đã hết hạn");
                return;
            }
            
            // Lấy username từ token
            String username = jwtUtils.getUserNameFromJwtToken(token);
            
            // Tạo xác thực cho Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Could not set user authentication: {}", e.getMessage());
            sendErrorResponse(response, "Lỗi xác thực: " + e.getMessage());
        }
    }
    
    /**
     * Trích xuất token từ request, ưu tiên lấy từ Authorization header,
     * nếu không có sẽ tìm trong cookies
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Thử lấy token từ header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // Nếu không có header, kiểm tra cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    private boolean isPublicUrl(String url) {
        return PUBLIC_URLS.stream().anyMatch(url::startsWith);
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message,
                new Date()
        );
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}