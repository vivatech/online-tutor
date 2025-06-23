package com.vivatech.onlinetutor.webchat.dto;


import com.vivatech.onlinetutor.webchat.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    
    private Long recipientId; // For direct messages
    
    private Long groupId; // For group messages
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private Message.MessageType messageType = Message.MessageType.TEXT;
    
    private String attachmentUrl;
}