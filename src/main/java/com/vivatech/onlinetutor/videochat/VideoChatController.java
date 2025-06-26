package com.vivatech.onlinetutor.videochat;

import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tutor/videochat")
public class VideoChatController {

    @Autowired
    private VideoChatProcessor processor;

    @PostMapping
    public Response createVideoMeeting(@RequestBody VideoChatDto dto) {
        return processor.createMeeting(dto, AppEnums.MeetingAggregator.ZOOM);
    }

    @DeleteMapping
    public Response deleteVideoMeeting(@RequestBody VideoChatDto dto) {
        processor.deleteMeeting(dto, AppEnums.MeetingAggregator.ZOOM);
        return Response.builder().status("SUCCESS").message("Meeting deleted.").build();
    }

}
