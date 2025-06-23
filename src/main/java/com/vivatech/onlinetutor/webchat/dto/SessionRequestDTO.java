package com.vivatech.onlinetutor.webchat.dto;

import com.vivatech.onlinetutor.model.TutorSession;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRequestDTO {
    private Integer id;

    @NotBlank(message = "Session title is required")
    @Size(max = 255, message = "Session title must not exceed 255 characters")
    private String sessionTitle;

    @NotNull(message = "Session type is required")
    private TutorSession.SessionType sessionType;

    @NotNull(message = "Session date is required")
    @Future(message = "Session date must be in the future")
    private LocalDate sessionDate;

    @NotNull(message = "Session end date is required")
    @Future(message = "Session end date must be in the future")
    private LocalDate sessionEndDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Time zone is required")
    private String timeZone;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private Boolean isRecurring = false;

    private TutorSession.RecurrenceFrequency recurrenceFrequency;

    @Min(value = 1, message = "Maximum students must be at least 1")
    private Integer maxStudents;

    @Min(value = 1, message = "Minimum enrollment must be at least 1")
    private Integer minEnrollment;

    private LocalDate enrollmentDeadline;

    private Set<String> learningObjectives;

    private Set<TutorSession.TeachingMethod> teachingMethods;

    private Set<TutorSession.ToolRequired> toolsRequired;

    private String languageOfInstruction;

    private Double pricePerSession;

    private String discountCoupon;

    private String paymentGatewayLinked;

    private Boolean adminApprovalRequired = false;

    @NotNull(message = "Visibility is required")
    private TutorSession.Visibility visibility = TutorSession.Visibility.PUBLIC;

    private String createdBy;  //username of the person

    private List<LocalDate> upcomingDates;

    private List<String> dayOfWeek;

    @NotNull(message = "Subject is required")
    private String subject;
}
