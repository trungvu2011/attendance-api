package com.attendance.controller;

import com.attendance.dto.user.CreateUserDTO;
import com.attendance.dto.user.UpdateUserDTO;
import com.attendance.dto.user.UserDTO;
import com.attendance.entities.User;
import com.attendance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Đăng ký người dùng mới
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email đã được sử dụng!");
        }

        // Chuyển đổi DTO sang entity và lưu vào DB
        User user = createUserDTO.toEntity();
        User createdUser = userService.createUser(user);

        // Trả về DTO không chứa mật khẩu
        return new ResponseEntity<>(UserDTO.fromEntity(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            UserDTO userDTO = UserDTO.fromEntity(userOpt.get());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Kiểm tra xem có phải người dùng hiện tại hay ADMIN không
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        if (!currentEmail.equals(userOpt.get().getEmail()) && !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền sửa thông tin người dùng này!");
        }

        // Kiểm tra xem email mới đã tồn tại chưa (nếu email thay đổi)
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().isEmpty() &&
                !updateUserDTO.getEmail().equals(userOpt.get().getEmail()) &&
                userService.existsByEmail(updateUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        User user = userOpt.get();
        updateUserDTO.updateEntity(user);
        User updatedUser = userService.updateUser(user);

        return new ResponseEntity<>(UserDTO.fromEntity(updatedUser), HttpStatus.OK);
    }

    // Xóa User
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        if (userService.getUserById(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Tìm kiếm user theo tên hoặc email
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        List<User> users = userService.searchUsers(name, email);
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/citizen-id/{citizenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserByCitizenId(@PathVariable String citizenId) {
        Optional<User> userOpt = userService.getUserByCitizenId(citizenId);
        if (userOpt.isPresent()) {
            UserDTO userDTO = UserDTO.fromEntity(userOpt.get());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Lấy thông tin profile người dùng hiện tại
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> userOpt = userService.getUserByEmail(currentEmail);
        if (userOpt.isPresent()) {
            UserDTO userDTO = UserDTO.fromEntity(userOpt.get());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        boolean success = userService.changePassword(currentEmail, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } else {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác");
        }
    }
}
