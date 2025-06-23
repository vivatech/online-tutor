package com.vivatech.onlinetutor.webchat.dto;

import com.vivatech.onlinetutor.webchat.model.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDto {
    private Long id;
    private UserDto user;
    private GroupMember.MemberRole role;
    private LocalDateTime joinedAt;
}