package com.ql.giapha.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ql.giapha.dto.JwtResponse;
import com.ql.giapha.dto.LoginRequest;
import com.ql.giapha.model.AppUser;
import com.ql.giapha.repository.UserRepo;
import com.ql.giapha.service.UserDetailsImpl;
import com.ql.giapha.util.JwtUtil;

import lombok.Data;

@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        String jwt = jwtUtils.generateToken(userDetails.getUsername());
        JwtResponse res = new JwtResponse();
        res.setRoles(roles);
        res.setToken(jwt);
        res.setId(userDetails.getId());
        res.setUsername(userDetails.getUsername());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/getInfo")
    public ResponseEntity<?> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);
        UserDTO userDTO = new UserDTO();
        userDTO.setFullname(user.getFullname());
        userDTO.setUsername(user.getUsername());
        userDTO.setJoinAt(user.getJoinAt());

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Kiểm tra mật khẩu hiện tại
            if (passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
                // Nếu mật khẩu hiện tại đúng, thay đổi mật khẩu mới và lưu vào cơ sở dữ liệu
                String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
                user.setPassword(newPassword);
                userRepo.save(user);
                return ResponseEntity.ok("Password changed successfully.");
            } else {
                return ResponseEntity.badRequest().body("Current password is incorrect.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody UpdateInfoRequest updateInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Kiểm tra mật khẩu hiện tại

            // Nếu mật khẩu hiện tại đúng, thay đổi mật khẩu mới và lưu vào cơ sở dữ liệu

            user.setFullname(updateInfoRequest.getNewname());
            userRepo.save(user);
            return ResponseEntity.ok("Info changed successfully.");

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("11111111");
    }
}

@Data
class UserDTO {
    private String fullname;
    private String username;
    private Date joinAt;
}

@Data
class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    // Getters and setters
}

@Data
class UpdateInfoRequest {
    private String newname;

    // Getters and setters
}