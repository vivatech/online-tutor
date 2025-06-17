package com.vivatech.online_tutor.webchat.service;

import com.vivatech.online_tutor.webchat.dto.ChatGroupDto;
import com.vivatech.online_tutor.webchat.dto.CreateGroupRequest;
import com.vivatech.online_tutor.webchat.dto.GroupMemberDto;
import com.vivatech.online_tutor.webchat.model.ChatGroup;
import com.vivatech.online_tutor.webchat.model.GroupMember;
import com.vivatech.online_tutor.webchat.model.User;
import com.vivatech.online_tutor.webchat.repository.ChatGroupRepository;
import com.vivatech.online_tutor.webchat.repository.GroupMemberRepository;
import com.vivatech.online_tutor.webchat.repository.UserRepository;
import com.vivatech.online_tutor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatGroupService {
    
    private final ChatGroupRepository chatGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    
    public ChatGroupDto createGroup(CreateGroupRequest request) {
        User creator = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("Creator user not found"));
        
        ChatGroup group = new ChatGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(creator);
        group.setMaxMembers(request.getMaxMembers());
        group.setIsActive(true);
        
        ChatGroup savedGroup = chatGroupRepository.save(group);
        
        // Add creator as admin member
        GroupMember creatorMember = new GroupMember();
        creatorMember.setGroup(savedGroup);
        creatorMember.setUser(creator);
        creatorMember.setRole(GroupMember.MemberRole.ADMIN);
        groupMemberRepository.save(creatorMember);
        
        return convertToDto(savedGroup);
    }
    
    public Optional<ChatGroupDto> getGroupById(Long id) {
        return chatGroupRepository.findById(id)
                .filter(group -> group.getIsActive())
                .map(this::convertToDto);
    }
    
    public List<ChatGroupDto> getAllActiveGroups() {
        return chatGroupRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ChatGroupDto> getGroupsByUserId(Long userId) {
        return chatGroupRepository.findGroupsByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ChatGroupDto> searchGroups(String searchTerm) {
        return chatGroupRepository.searchActiveGroups(searchTerm)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ChatGroupDto addMemberToGroup(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!group.getIsActive()) {
            throw new RuntimeException("Group is not active");
        }
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        // Check if group has reached max capacity
        Long currentMemberCount = groupMemberRepository.countByGroupId(groupId);
        if (currentMemberCount >= group.getMaxMembers()) {
            throw new RuntimeException("Group has reached maximum capacity");
        }
        
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupMember.MemberRole.MEMBER);
        groupMemberRepository.save(member);
        
        return convertToDto(group);
    }
    
    public void removeMemberFromGroup(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        groupMemberRepository.delete(member);
    }
    
    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return groupMemberRepository.existsByGroupAndUser(group, user);
    }
    
    public void deactivateGroup(Long id) {
        ChatGroup group = chatGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        group.setIsActive(false);
        chatGroupRepository.save(group);
    }
    
    private ChatGroupDto convertToDto(ChatGroup group) {
        ChatGroupDto dto = new ChatGroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedBy(userService.getUserById(group.getCreatedBy().getId()).orElse(null));
        dto.setIsActive(group.getIsActive());
        dto.setMaxMembers(group.getMaxMembers());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        
        // Get current member count
        Long memberCount = groupMemberRepository.countByGroupId(group.getId());
        dto.setCurrentMemberCount(memberCount.intValue());
        
        // Get members
        List<GroupMemberDto> members = groupMemberRepository.findByGroupId(group.getId())
                .stream()
                .map(this::convertMemberToDto)
                .collect(Collectors.toList());
        dto.setMembers(members);
        
        return dto;
    }
    
    private GroupMemberDto convertMemberToDto(GroupMember member) {
        GroupMemberDto dto = new GroupMemberDto();
        dto.setId(member.getId());
        dto.setUser(userService.getUserById(member.getUser().getId()).orElse(null));
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }
}