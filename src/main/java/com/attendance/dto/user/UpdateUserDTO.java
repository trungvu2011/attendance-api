package com.attendance.dto.user;

import com.attendance.entities.User;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String name;
    
    @Email(message = "Email không hợp lệ")
    private String email;

    private LocalDate birth;
    private String password;
    private String citizenId;
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
        if(this.birth != null) {
            user.setBirth(this.birth);
        }
        if(this.citizenId != null) {
            user.setCitizenId(this.citizenId);
        }
        if(this.role != null) {
            user.setRole(this.role);
        }
    }
}