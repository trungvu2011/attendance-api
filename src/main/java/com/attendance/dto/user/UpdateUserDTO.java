package com.attendance.dto.user;

import com.attendance.entities.User;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String name;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String password;
    private String citizenId;
    private String faceImage;
    private User.Role role;
    
    // Cập nhật thông tin từ DTO vào Entity
    public void updateEntity(User user) {
        if(this.name != null && !this.name.isEmpty()) {
            user.setName(this.name);
        }
        if(this.email != null && !this.email.isEmpty()) {
            user.setEmail(this.email);
        }
        if(this.password != null && !this.password.isEmpty()) {
            user.setPassword(this.password);
        }
        if(this.citizenId != null) {
            user.setCitizenId(this.citizenId);
        }
        if(this.faceImage != null) {
            user.setFaceImage(this.faceImage);
        }
        if(this.role != null) {
            user.setRole(this.role);
        }
    }
}