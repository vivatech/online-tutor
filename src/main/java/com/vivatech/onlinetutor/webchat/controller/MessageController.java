package com.vivatech.onlinetutor.webchat.controller;

import com.vivatech.onlinetutor.webchat.dto.MessageDto;
import com.vivatech.onlinetutor.webchat.dto.SendMessageRequest;
import com.vivatech.onlinetutor.webchat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management", description = "APIs for managing messages")
public class MessageController {
    
    private final MessageService messageService;
    
    @PostMapping
    @Operation(summary = "Send a message")
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        MessageDto message = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    
    @GetMapping("/direct")
    @Operation(summary = "Get direct messages between two users")
    public ResponseEntity<Page<MessageDto>> getDirectMessages(@RequestParam Long userId1,
                                                             @RequestParam Long userId2,
                                                             Pageable pageable) {
        Page<MessageDto> messages = messageService.getDirectMessages(userId1, userId2, pageable);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get group messages")
    public ResponseEntity<Page<MessageDto>> getGroupMessages(@PathVariable Long groupId,
                                                           Pageable pageable) {
        Page<MessageDto> messages = messageService.getGroupMessages(groupId, pageable);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/unread/{userId}")
    @Operation(summary = "Get unread messages for user")
    public ResponseEntity<List<MessageDto>> getUnreadMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/unread-count/{userId}")
    @Operation(summary = "Get unread message count for user")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable Long userId) {
        Long count = messageService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/recent/{userId}")
    @Operation(summary = "Get recent direct messages for user")
    public ResponseEntity<List<MessageDto>> getRecentDirectMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getRecentDirectMessages(userId);
        return ResponseEntity.ok(messages);
    }
    
    @PutMapping("/{messageId}/read")
    @Operation(summary = "Mark message as read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/read")
    @Operation(summary = "Mark multiple messages as read")
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody List<Long> messageIds) {
        messageService.markMessagesAsRead(messageIds);
        return ResponseEntity.noContent().build();
    }
}