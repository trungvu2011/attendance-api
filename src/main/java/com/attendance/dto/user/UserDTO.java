package com.attendance.dto.user;

import com.attendance.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    
    @NotBlank(message = "Tên không được để trống")
    private String name;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private LocalDate birth;
    private String citizenId;
    private String faceImage;
    private User.Role role;
    
    // Constructor để chuyển từ entity sang DTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBirth(user.getBirth());
        dto.setCitizenId(user.getCitizenId());
        dto.setRole(user.getRole());
        return dto;
    }
}