package com.vivatech.onlinetutor.videochat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/zoom/webhook")
public class ZoomCallback {

    @PostMapping("/access-token")
    public String accessToken(@RequestBody String jsonString) {
        log.info("Access token: {}", jsonString);
        return jsonString;
    }

}
