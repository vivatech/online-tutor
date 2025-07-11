package com.vivatech.onlinetutor.webchat.controller;

import com.vivatech.onlinetutor.webchat.dto.ChatGroupDto;
import com.vivatech.onlinetutor.webchat.dto.CreateGroupRequest;
import com.vivatech.onlinetutor.webchat.service.ChatGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/groups")
@RequiredArgsConstructor
@Tag(name = "Group Management", description = "APIs for managing chat groups")
public class ChatGroupController {
    
    private final ChatGroupService chatGroupService;
    
    @PostMapping
    @Operation(summary = "Create a new chat group")
    public ResponseEntity<ChatGroupDto> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        ChatGroupDto group = chatGroupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.OK).body(group);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID")
    public ResponseEntity<ChatGroupDto> getGroupById(@PathVariable Long id) {
        return chatGroupService.getGroupById(id)
                .map(group -> ResponseEntity.ok(group))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get all active groups")
    public ResponseEntity<List<ChatGroupDto>> getAllGroups() {
        List<ChatGroupDto> groups = chatGroupService.getAllActiveGroups();
        return ResponseEntity.ok(groups);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get groups by user ID")
    public ResponseEntity<List<ChatGroupDto>> getGroupsByUserId(@PathVariable Long userId) {
        List<ChatGroupDto> groups = chatGroupService.getGroupsByUserId(userId);
        return ResponseEntity.ok(groups);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search groups")
    public ResponseEntity<List<ChatGroupDto>> searchGroups(@RequestParam String term) {
        List<ChatGroupDto> groups = chatGroupService.searchGroups(term);
        return ResponseEntity.ok(groups);
    }
    
    @PostMapping("/{groupId}/members/{userId}")
    @Operation(summary = "Add member to group")
    public ResponseEntity<ChatGroupDto> addMemberToGroup(@PathVariable Long groupId, 
                                                       @PathVariable Long userId) {
        ChatGroupDto group = chatGroupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok(group);
    }
    
    @DeleteMapping("/{groupId}/members/{userId}")
    @Operation(summary = "Remove member from group")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long groupId, 
                                                     @PathVariable Long userId) {
        chatGroupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{groupId}/members/{userId}/check")
    @Operation(summary = "Check if user is member of group")
    public ResponseEntity<Boolean> isUserMemberOfGroup(@PathVariable Long groupId, 
                                                      @PathVariable Long userId) {
        boolean isMember = chatGroupService.isUserMemberOfGroup(groupId, userId);
        return ResponseEntity.ok(isMember);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate group")
    public ResponseEntity<Void> deactivateGroup(@PathVariable Long id) {
        chatGroupService.deactivateGroup(id);
        return ResponseEntity.noContent().build();
    }
}