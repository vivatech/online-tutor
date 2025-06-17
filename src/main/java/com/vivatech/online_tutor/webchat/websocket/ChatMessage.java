package com.vivatech.online_tutor.webchat.websocket;

import com.vivatech.online_tutor.webchat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private Long groupId;
    private String content;
    private Message.MessageType messageType;
    private String attachmentUrl;
    private LocalDateTime timestamp;
    private MessageStatus status;
    
    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
}