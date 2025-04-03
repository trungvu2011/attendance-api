package com.attendance.service;

import com.attendance.entities.User;
import com.attendance.repositories.AttendanceRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() { return userRepository.findAll();}

    public Optional<User> getUserById(UUID userId){ return  userRepository.findById(userId);}

    // Tạo mới bản ghi User
    public User createUser(User user){ return userRepository.save(user); }

    // Cap nhat thong tin
    public User updateUser(User user){ return userRepository.save(user);}

    // Xoa user
    public void deleteUser(UUID userId){ userRepository.deleteById(userId);}
}
