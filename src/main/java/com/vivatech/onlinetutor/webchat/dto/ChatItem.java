package com.vivatech.onlinetutor.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatItem {
    private Long id;
    private String type;
    private String name;
    private String avatar;
    private Integer unreadCount;

}
