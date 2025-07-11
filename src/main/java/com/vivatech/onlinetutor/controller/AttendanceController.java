package com.vivatech.onlinetutor.controller;


import com.vivatech.onlinetutor.attendance.*;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.model.Attendance;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.repository.AttendanceRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import org.apache.commons.lang3.StringUtils;
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
                                                                       @RequestParam LocalDate endDate,
                                                                       @RequestParam(required = false) String studentName) {
        List<Attendance> attendanceList = attendanceRepository.findBySessionRegistrationIdIn(List.of(sessionId));
        List<AttendanceStatus> statusList = attendanceList.stream()
                .filter(ele -> ele.getDate().isAfter(startDate.minusDays(1)) && ele.getDate().isBefore(endDate.plusDays(1)))
                .map(obj -> AttendanceStatus.builder()
                        .date(obj.getDate())
                        .presentCount(obj.getPresent() ? 1 : 0)
                        .absentCount(obj.getPresent() ? 0 : 1)
                        .name(obj.getSessionRegistration().getStudentName())
                        .email(obj.getSessionRegistration().getStudentEmail())
                        .registrationId(obj.getSessionRegistration().getId())
                        .build()).toList();
        if (statusList.isEmpty()) return ResponseEntity.noContent().build();
        if (!StringUtils.isEmpty(studentName)) {
            String lowerCaseName = studentName.toLowerCase();
            statusList = statusList.stream()
                    .filter(ele -> ele.getName().toLowerCase().contains(lowerCaseName))
                    .toList();
        }
        return ResponseEntity.ok(statusList);
    }

    @GetMapping("/get-registration-and-attendance-detail")
    public ResponseEntity<RegistrationAndAttendanceResponse> getRegistrationAndAttendanceDetail(@RequestParam Integer registrationId,
                                                                                                @RequestParam LocalDate attendanceDate) {
        SessionRegistration sessionRegistration = sessionRegistrationRepository.findById(registrationId).orElseThrow(() -> new OnlineTutorExceptionHandler("Registration not found with ID: " + registrationId));
        Attendance attendance = attendanceRepository.findBySessionRegistrationIdAndDate(sessionRegistration.getId(), attendanceDate);
        AttendanceStatus attendanceStatus = null;
        if (attendance != null) {
            attendanceStatus = AttendanceStatus.builder()
                    .date(attendance.getDate())
                    .presentCount(attendance.getPresent() ? 1 : 0)
                    .absentCount(attendance.getPresent() ? 0 : 1)
                    .name(attendance.getSessionRegistration().getStudentName())
                    .email(attendance.getSessionRegistration().getStudentEmail())
                    .registrationId(attendance.getSessionRegistration().getId())
                    .build();
        }
        RegistrationAndAttendanceResponse response = RegistrationAndAttendanceResponse.builder()
                .sessionRegistration(sessionRegistration)
                .attendanceStatus(attendanceStatus)
                .build();
        return ResponseEntity.ok(response);
    }
}
