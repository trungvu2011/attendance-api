package com.attendance.service;

import com.attendance.entities.User;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() { return userRepository.findAll();}

    public Optional<User> getUserById(UUID userId){ return userRepository.findById(userId);}
    
    public Optional<User> getUserByEmail(String email) { return userRepository.findByEmail(email); }

    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }

    // Tạo mới bản ghi User - Mã hóa mật khẩu trước khi lưu 
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user); 
    }

    // Cap nhat thong tin - Kiểm tra xem có cần mã hóa mật khẩu không
    public User updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            Optional<User> existingUser = userRepository.findById(user.getUserId());
            if (existingUser.isPresent()) {
                String currentPassword = existingUser.get().getPassword();
                // Nếu mật khẩu mới khác mật khẩu cũ và không bắt đầu bằng $2a$ (định dạng của BCrypt)
                if (!user.getPassword().equals(currentPassword) && !user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
        }
        return userRepository.save(user);
    }

    // Xoa user
    public void deleteUser(UUID userId){ userRepository.deleteById(userId);}
    
    // Tìm kiếm user theo tên hoặc email
    public List<User> searchUsers(String name, String email) {
        User example = new User();
        
        if (name != null && !name.isEmpty()) {
            example.setName(name);
        }
        
        if (email != null && !email.isEmpty()) {
            example.setEmail(email);
        }
        
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();
                
        return userRepository.findAll(Example.of(example, matcher));
    }
    
    // Đổi mật khẩu
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Kiểm tra mật khẩu cũ
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }
}
