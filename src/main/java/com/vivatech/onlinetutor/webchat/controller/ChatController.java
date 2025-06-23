package com.vivatech.onlinetutor.webchat.controller;

import com.vivatech.onlinetutor.webchat.dto.MessageDto;
import com.vivatech.onlinetutor.webchat.dto.SendMessageRequest;
import com.vivatech.onlinetutor.webchat.service.MessageService;
import com.vivatech.onlinetutor.webchat.websocket.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload SendMessageRequest request) {
        try {
            // Save message to database
            MessageDto savedMessage = messageService.sendMessage(request);

            // Create WebSocket message
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(savedMessage.getId());
            chatMessage.setSenderId(savedMessage.getSender().getId());
            chatMessage.setSenderName(savedMessage.getSender().getFullName());
            chatMessage.setContent(savedMessage.getContent());
            chatMessage.setMessageType(savedMessage.getMessageType());
            chatMessage.setAttachmentUrl(savedMessage.getAttachmentUrl());
            chatMessage.setTimestamp(savedMessage.getSentAt());
            chatMessage.setStatus(ChatMessage.MessageStatus.SENT);

            if (request.getGroupId() != null) {
                // Group message - send to all group members
                chatMessage.setGroupId(request.getGroupId());
                messagingTemplate.convertAndSend("/topic/group/" + request.getGroupId(), chatMessage);
            } else if (request.getRecipientId() != null) {
                // Direct message - send to specific user
                chatMessage.setRecipientId(request.getRecipientId());

                // Send to recipient
                messagingTemplate.convertAndSendToUser(
                        request.getRecipientId().toString(),
                        "/queue/messages",
                        chatMessage
                );

                // Send to sender for confirmation (so sender sees their own message)
                messagingTemplate.convertAndSendToUser(
                        request.getSenderId().toString(),
                        "/queue/messages",
                        chatMessage
                );
            }

            log.info("Message sent successfully: {}", chatMessage);

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);

            // Send error message back to sender
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setContent("Failed to send message: " + e.getMessage());
            errorMessage.setStatus(ChatMessage.MessageStatus.SENT);
            errorMessage.setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSendToUser(
                    request.getSenderId().toString(),
                    "/queue/errors",
                    errorMessage
            );
        }
    }
    
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {
        // Handle user joining (for presence/status)
        log.info("User {} joined the chat", chatMessage.getSenderName());
        
        // You can broadcast user joined notification if needed
        chatMessage.setContent(chatMessage.getSenderName() + " joined the chat");
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Broadcast to all users or specific groups as needed
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}