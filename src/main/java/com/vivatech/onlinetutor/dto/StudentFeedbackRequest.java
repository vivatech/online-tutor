package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.model.StudentFeedback;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeedbackRequest {

    @NotNull(message = "registration id is required")
    private Integer sessionRegistrationId;

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

    @NotNull(message = "created by is required")
    private String createdBy;
}
