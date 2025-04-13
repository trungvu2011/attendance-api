package com.attendance.util;

import com.attendance.security.jwt.JwtUtils;
import com.attendance.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class để truy xuất thông tin từ JWT token
 */
@Component
public class TokenUtil {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Lấy ID của người dùng hiện tại từ JWT token
     * @return UUID của người dùng hiện tại hoặc null nếu không xác thực
     */
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        return null;
    }

    /**
     * Lấy email của người dùng hiện tại từ JWT token
     * @return Email của người dùng hoặc null nếu không xác thực
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Kiểm tra JWT token có hợp lệ không
     * @param token JWT token cần kiểm tra
     * @return true nếu token hợp lệ, false nếu không
     */
    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }

    /**
     * Lấy thông tin người dùng từ token
     * @param token JWT token 
     * @return Username (email) được lưu trong token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtils.getUserNameFromJwtToken(token);
    }
}