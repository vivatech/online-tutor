package com.vivatech.onlinetutor.videochat.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZoomCreateMeetingRequest {

    @JsonProperty("agenda")
    private String agenda;

    @JsonProperty("default_password")
    private boolean defaultPassword;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("password")
    private String password;

    @JsonProperty("pre_schedule")
    private boolean preSchedule;

    @JsonProperty("schedule_for")
    private String scheduleFor;

    @JsonProperty("start_time")
    private Date startTime;

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("type")
    private int type;

    @JsonProperty("recurrence")
    private ZoomRecurrenceDto recurrence;

}
