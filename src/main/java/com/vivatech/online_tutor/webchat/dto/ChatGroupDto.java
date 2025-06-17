package com.vivatech.online_tutor.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupDto {
    private Long id;
    private String name;
    private String description;
    private UserDto createdBy;
    private Boolean isActive;
    private Integer maxMembers;
    private Integer currentMemberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupMemberDto> members;
}