package com.vivatech.onlinetutor.attendance;

import lombok.Data;

@Data
public class AttendanceDto {
    private Integer sessionRegistrationId;
    private Boolean present;
}
