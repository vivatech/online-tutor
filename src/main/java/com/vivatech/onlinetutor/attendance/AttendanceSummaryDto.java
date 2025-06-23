package com.vivatech.onlinetutor.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryDto {
    private int totalPresent;
    private int totalAbsent;
    List<AttendanceStatus> attendanceStatus;
}
