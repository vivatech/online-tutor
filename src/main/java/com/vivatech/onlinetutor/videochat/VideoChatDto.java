package com.vivatech.onlinetutor.videochat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoChatDto {
    private Integer id;
    private String meetingId;
    private String meetingTitle;
    private LocalDate meetingStartDate;
    private String meetingStartTime;
    private LocalDate meetingEndDate;
    private boolean isRecurring;
    private String meetingEndTime;
    private Integer meetingDuration;
    private String hostEmail;
    private String createdBy;
}
