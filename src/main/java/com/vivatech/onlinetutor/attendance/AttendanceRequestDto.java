package com.vivatech.onlinetutor.attendance;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceRequestDto {
    private LocalDate date;
    private List<AttendanceDto> attendance;
}
