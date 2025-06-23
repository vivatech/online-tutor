package com.vivatech.onlinetutor.webchat.dto;


import com.vivatech.onlinetutor.webchat.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    private String username;

    private String msisdn;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "User role is required")
    private User.UserRole role;
    
    private String profilePicture;
}