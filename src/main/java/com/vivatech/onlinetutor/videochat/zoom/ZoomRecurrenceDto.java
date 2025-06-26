package com.vivatech.onlinetutor.videochat.zoom;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoomRecurrenceDto {

    @JsonProperty("end_date_time")
    private Date endDateTime;

    @JsonProperty("type")
    private Integer type;
}
