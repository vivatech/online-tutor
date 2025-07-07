package com.vivatech.onlinetutor.webchat.service;

import com.vivatech.onlinetutor.webchat.dto.ChatItem;
import com.vivatech.onlinetutor.webchat.dto.MessageDto;
import com.vivatech.onlinetutor.webchat.dto.SendMessageRequest;
import com.vivatech.onlinetutor.webchat.dto.UserDto;
import com.vivatech.onlinetutor.webchat.model.ChatGroup;
import com.vivatech.onlinetutor.webchat.model.Message;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.ChatGroupRepository;
import com.vivatech.onlinetutor.webchat.repository.MessageRepository;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ChatGroupService chatGroupService;
    private final UserService userService;
    
    public MessageDto sendMessage(SendMessageRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        Message message = new Message();
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setIsRead(false);
        
        // Determine if it's a direct message or group message
        if (request.getGroupId() != null) {
            // Group message
            ChatGroup group = chatGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            if (!group.getIsActive()) {
                throw new RuntimeException("Group is not active");
            }
            
            // Check if sender is a member of the group
            if (!chatGroupService.isUserMemberOfGroup(request.getGroupId(), request.getSenderId())) {
                throw new RuntimeException("Sender is not a member of this group");
            }
            
            message.setGroup(group);
        } else if (request.getRecipientId() != null) {
            // Direct message
            User recipient = userRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            
            if (!recipient.getIsActive()) {
                throw new RuntimeException("Recipient is not active");
            }
            
            message.setRecipient(recipient);
        } else {
            throw new RuntimeException("Either recipientId or groupId must be provided");
        }
        
        Message savedMessage = messageRepository.save(message);
        return convertToDto(savedMessage);
    }
    
    public Page<MessageDto> getDirectMessages(Long userId1, Long userId2, Pageable pageable) {
        return messageRepository.findDirectMessagesBetweenUsers(userId1, userId2, pageable)
                .map(this::convertToDto);
    }
    
    public Page<MessageDto> getGroupMessages(Long groupId, Pageable pageable) {
        return messageRepository.findMessagesByGroupId(groupId, pageable)
                .map(this::convertToDto);
    }
    
    public List<MessageDto> getUnreadMessages(Long userId) {
        return messageRepository.findUnreadMessagesByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesByUserId(userId);
    }
    
    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
    
    public void markMessagesAsRead(List<Long> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        messages.forEach(message -> {
            if (!message.getIsRead()) {
                message.setIsRead(true);
                message.setReadAt(LocalDateTime.now());
            }
        });
        messageRepository.saveAll(messages);
    }
    
    public List<MessageDto> getRecentDirectMessages(Long userId) {
        return messageRepository.findRecentDirectMessages(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Set<ChatItem> getChatUserList(Long userId) {
        List<MessageDto> messages = messageRepository.findRecentDirectMessages(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
        Set<ChatItem> chatItems = new HashSet<>();
        Map<Long, Integer> unreadCount = new HashMap<>();
        messages.forEach(message -> {
            UserDto otherUserId = Objects.equals(message.getSender().getId(), userId) ? message.getRecipient() : message.getSender();
            if (otherUserId != null) {
                unreadCount.put(otherUserId.getId(), unreadCount.getOrDefault(otherUserId.getId(), 0) + 1);
                ChatItem chatItem = ChatItem.builder()
                        .id(otherUserId.getId())
                        .name(otherUserId.getFullName())
                        .avatar(otherUserId.getProfilePicture())
                        .build();
                chatItems.add(chatItem);
            }
        });
        chatItems.forEach(chatItem -> {
            chatItem.setUnreadCount(unreadCount.getOrDefault(chatItem.getId(), 0));
            chatItem.setType("direct-message");
        });
        return chatItems;
    }
    
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSender(userService.getUserById(message.getSender().getId()).orElse(null));
        
        if (message.getRecipient() != null) {
            dto.setRecipient(userService.getUserById(message.getRecipient().getId()).orElse(null));
        }
        
        if (message.getGroup() != null) {
            dto.setGroupId(message.getGroup().getId());
            dto.setGroupName(message.getGroup().getName());
        }
        
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setIsRead(message.getIsRead());
        dto.setSentAt(message.getSentAt());
        dto.setReadAt(message.getReadAt());
        
        return dto;
    }
}