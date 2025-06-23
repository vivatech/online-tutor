package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.model.StudentFeedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeedbackResponse {

    private Integer id;
    private Integer sessionRegistrationId;
    private Long tutorId;
    private String tutorName;
    private String courseTitle;
    private String sessionId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private Integer sessionDuration;
    private String studentName;
    private Integer studentId;

    // Attendance
    private StudentFeedback.AttendanceStatus attendanceStatus;
    private String tutorNotesAttendance;

    // Engagement & Participation
    private StudentFeedback.ObservedBehaviors observedBehaviors;
    private StudentFeedback.EngagementLevel engagementLevel;

    // Comprehension & Understanding
    private StudentFeedback.UnderstandingLevel understandingLevel;
    private String comprehensionIssues;

    // Assignments & Work Submission
    private Boolean assignmentGiven;
    private StudentFeedback.AssignmentType assignmentType;
    private String assignmentDeadline;
    private Boolean submissionReceived;
    private StudentFeedback.WorkQuality workQuality;

    // Feedback to Student
    private String academicStrengths;
    private String areasForImprovement;
    private String recommendedFocus;

    // Parental Follow-up
    private String suggestedMessageParent;
    private Boolean followUpMeetingRequested;
    private String followUpReason;

    // Session Review Summary
    private StudentFeedback.SessionRating overallSessionRating;
    private String additionalTutorComments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
