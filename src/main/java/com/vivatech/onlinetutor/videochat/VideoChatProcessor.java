package com.vivatech.onlinetutor.videochat;

import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoChatProcessor {

    private final List<VideoChatInterface> sortedProcessors;

    public VideoChatProcessor(List<VideoChatInterface> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public VideoChatInterface getMatchedProcessor(AppEnums.MeetingAggregator videoChatMode) {
        for (VideoChatInterface processor : sortedProcessors) {
            if(processor.supports(videoChatMode)) {
                return processor;
            }
        }
        return null;
    }

    public Response createMeeting(VideoChatDto videoChatDto, AppEnums.MeetingAggregator mode) {
        VideoChatInterface matchedProcessor = getMatchedProcessor(mode);
        return matchedProcessor.createMeeting(videoChatDto);
    }

    public void deleteMeeting(VideoChatDto videoChatDto, AppEnums.MeetingAggregator mode) {
        VideoChatInterface matchedProcessor = getMatchedProcessor(mode);
        matchedProcessor.deleteMeeting(videoChatDto);
    }
}
