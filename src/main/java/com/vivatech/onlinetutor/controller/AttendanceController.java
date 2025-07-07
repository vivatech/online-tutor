package com.vivatech.onlinetutor.controller;


import com.vivatech.onlinetutor.attendance.AttendanceDto;
import com.vivatech.onlinetutor.attendance.AttendanceRequestDto;
import com.vivatech.onlinetutor.attendance.AttendanceStatus;
import com.vivatech.onlinetutor.attendance.AttendanceSummaryDto;
import com.vivatech.onlinetutor.model.Attendance;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.repository.AttendanceRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/attendance")
public class AttendanceController {

    @Autowired
    private SessionRegistrationRepository sessionRegistrationRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitAttendance(@RequestBody AttendanceRequestDto request) {
        for (AttendanceDto dto : request.getAttendance()) {
            SessionRegistration registrationOpt = sessionRegistrationRepository.findById(dto.getSessionRegistrationId()).orElse(null);
            if (registrationOpt == null) continue;
            Attendance existingAttendance = attendanceRepository.findBySessionRegistrationIdAndDate(dto.getSessionRegistrationId(), request.getDate());
            if (existingAttendance != null) continue;
            Attendance attendance = new Attendance();
            attendance.setDate(request.getDate());
            attendance.setPresent(dto.getPresent());
            attendance.setSessionRegistration(registrationOpt);
            attendanceRepository.save(attendance);
        }
        return ResponseEntity.ok("Attendance submitted successfully");
    }

    @GetMapping("/history/summary")
    public ResponseEntity<AttendanceSummaryDto> getAttendanceSummary(@RequestParam Integer registrationId,
                                                     @RequestParam LocalDate startDate,
                                                     @RequestParam LocalDate endDate) {
        List<Object[]> results = attendanceRepository.getAttendanceSummary(registrationId, startDate, endDate);
        List<AttendanceStatus> statusList = results.stream()
                .map(obj -> AttendanceStatus.builder()
                        .date((LocalDate) obj[0])
                        .presentCount(((Number) obj[1]).intValue())
                        .absentCount(((Number) obj[2]).intValue())
                        .build()).toList();

        //Calculating total present and total absent
        List<Attendance> attendances = attendanceRepository.findBySessionRegistrationId(registrationId);
        int totalPresent = attendances.stream().mapToInt(ele -> ele.getPresent() ? 1 : 0).sum();
        int totalAbsent = attendances.stream().mapToInt(ele -> !ele.getPresent() ? 1 : 0).sum();

        return ResponseEntity.ok(new AttendanceSummaryDto(totalPresent, totalAbsent, statusList));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AttendanceStatus>> getAttendanceHistory(@RequestParam Integer sessionId,
                                                       @RequestParam LocalDate startDate,
                                                       @RequestParam LocalDate endDate) {
        List<Object[]> results = attendanceRepository.getAttendanceListByEvent(sessionId, startDate, endDate);
        List<AttendanceStatus> statusList = results.stream()
                .map(obj -> AttendanceStatus.builder()
                        .date((LocalDate) obj[0])
                        .presentCount(((Number) obj[1]).intValue())
                        .absentCount(((Number) obj[2]).intValue())
                        .name((String) obj[3])
                        .email((String) obj[4])
                        .build()).toList();
        if (statusList.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(statusList);
    }
}
