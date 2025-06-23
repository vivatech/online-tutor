package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.AuthDto;
import com.vivatech.onlinetutor.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/tutor/users")
public class LoginController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthDto login(@RequestBody AuthDto authDto) {

        if (authDto != null) {
            return null;
        } else {
            return new AuthDto("FAILED", "Invalid credentials");
        }
    }

    @PostMapping("/verify-otp")
    public AuthDto verifyOtp(@RequestBody AuthDto authDto) {
        return new AuthDto();
    }

    @PostMapping("/forgot-password")
    public AuthDto forgotPassword(@RequestBody AuthDto authDto) {
        return new AuthDto();
    }

    @PostMapping("/verify-forgot-password-otp")
    public AuthDto verifyForgotPasswordOtp(@RequestBody AuthDto authDto) {
        return new AuthDto();
    }
}
