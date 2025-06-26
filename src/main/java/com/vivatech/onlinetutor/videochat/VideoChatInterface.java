package com.vivatech.onlinetutor.videochat;


import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;

public interface VideoChatInterface {

    boolean supports(AppEnums.MeetingAggregator aggregatorName);

    Response createMeeting(VideoChatDto videoChatDto);

    Response joinMeeting(VideoChatDto videoChatDto);

    void deleteMeeting(VideoChatDto videoChatDto);
}
