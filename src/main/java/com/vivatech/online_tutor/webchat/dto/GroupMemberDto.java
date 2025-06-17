package com.vivatech.online_tutor.webchat.dto;

import com.vivatech.online_tutor.webchat.model.GroupMember;
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