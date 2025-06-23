package com.vivatech.onlinetutor.webchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    
    @NotBlank(message = "Group name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Created by user ID is required")
    private Long createdByUserId;
    
    @Positive(message = "Max members must be positive")
    private Integer maxMembers = 50;
}