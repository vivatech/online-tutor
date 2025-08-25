package com.vivatech.onlinetutor.webchat.dto;

import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.videochat.MeetingResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDTO {
    private Integer id;
    private String sessionTitle;
    private TutorSession.SessionType sessionType;
    private LocalDate sessionDate;
    private LocalDate sessionEndDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timeZone;
    private Integer durationMinutes;
    private Boolean isRecurring;
    private TutorSession.RecurrenceFrequency recurrenceFrequency;
    private Integer maxStudents;
    private Integer minEnrollment;
    private LocalDate enrollmentDeadline;
    private Set<String> learningObjectives;
    private Set<TutorSession.TeachingMethod> teachingMethods;
    private Set<TutorSession.ToolRequired> toolsRequired;
    private String languageOfInstruction;
    private Double pricePerSession;
    private String discountCoupon;
    private String paymentGatewayLinked;
    private Boolean adminApprovalRequired;
    private TutorSession.Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LocalDate> upcomingDates = new ArrayList<>();
    private MeetingResponseDto meetingDto;
    private String sessionImage;
    private String subject;
    private String sessionStatus;
    private String createdByName;
}
