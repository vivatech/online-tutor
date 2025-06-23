package com.vivatech.onlinetutor.webchat.dto;

import com.vivatech.onlinetutor.webchat.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String msisdn;
    private String fullName;
    private String email;
    private User.UserRole role;
    private String profilePicture;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}