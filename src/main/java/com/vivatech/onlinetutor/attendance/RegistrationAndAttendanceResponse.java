package com.vivatech.onlinetutor.attendance;

import com.vivatech.onlinetutor.model.SessionRegistration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationAndAttendanceResponse {
    private SessionRegistration sessionRegistration;
    private AttendanceStatus attendanceStatus;
}
