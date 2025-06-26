package com.vivatech.onlinetutor.videochat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingResponseDto {
    private String joinUrl;
    private String meetingId;
    private String hostUrl;
}
