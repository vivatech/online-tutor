package com.vivatech.onlinetutor.webchat.dto;


import com.vivatech.onlinetutor.webchat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private UserDto sender;
    private UserDto recipient;
    private Long groupId;
    private String groupName;
    private String content;
    private Message.MessageType messageType;
    private String attachmentUrl;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}