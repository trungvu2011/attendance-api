package com.attendance.dto.user;

import com.attendance.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    @NotBlank(message = "Tên không được để trống")
    private String name;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
    
    private LocalDate birth;
    private String citizenId;
    private User.Role role = User.Role.CANDIDATE;
    
    // Phương thức chuyển đổi từ DTO sang Entity
    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setBirth(this.birth);
        user.setCitizenId(this.citizenId);
        user.setRole(this.role);
        return user;
    }
}