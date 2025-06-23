package com.vivatech.onlinetutor.model;

import com.vivatech.onlinetutor.webchat.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mumly_tutor_student_feedback")
public class StudentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "session_registration_id", nullable = false, unique = true)
    private SessionRegistration sessionRegistration;

    @ManyToOne
    private User tutor;

    // Attendance
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    private AttendanceStatus attendanceStatus;

    @Column(name = "tutor_notes_attendance", columnDefinition = "TEXT")
    private String tutorNotesAttendance;

    // Engagement & Participation
    @Enumerated(EnumType.STRING)
    @Column(name = "observed_behaviors")
    private ObservedBehaviors observedBehaviors;

    @Enumerated(EnumType.STRING)
    @Column(name = "engagement_level")
    private EngagementLevel engagementLevel;

    // Comprehension & Understanding
    @Enumerated(EnumType.STRING)
    @Column(name = "understanding_level")
    private UnderstandingLevel understandingLevel;

    @Column(name = "comprehension_issues", columnDefinition = "TEXT")
    private String comprehensionIssues;

    // Assignments & Work Submission
    @Column(name = "assignment_given")
    private Boolean assignmentGiven;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type")
    private AssignmentType assignmentType;

    @Column(name = "assignment_deadline")
    private String assignmentDeadline;

    @Column(name = "submission_received")
    private Boolean submissionReceived;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_quality")
    private WorkQuality workQuality;

    // Feedback to Student
    @Column(name = "academic_strengths", columnDefinition = "TEXT")
    private String academicStrengths;

    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(name = "recommended_focus", columnDefinition = "TEXT")
    private String recommendedFocus;

    // Parental Follow-up
    @Column(name = "suggested_message_parent", columnDefinition = "TEXT")
    private String suggestedMessageParent;

    @Column(name = "follow_up_meeting_requested")
    private Boolean followUpMeetingRequested;

    @Column(name = "follow_up_reason", columnDefinition = "TEXT")
    private String followUpReason;

    // Session Review Summary
    @Enumerated(EnumType.STRING)
    @Column(name = "overall_session_rating")
    private SessionRating overallSessionRating;

    @Column(name = "additional_tutor_comments", columnDefinition = "TEXT")
    private String additionalTutorComments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE
    }

    public enum ObservedBehaviors {
        ACTIVE_PARTICIPATION, PASSIVE_LISTENING, DISTRACTED, ENGAGED_QUESTIONING, COLLABORATIVE
    }

    public enum EngagementLevel {
        LOW, MODERATE, HIGH
    }

    public enum UnderstandingLevel {
        EXCELLENT, GOOD, AVERAGE, POOR
    }

    public enum AssignmentType {
        WRITTEN, VERBAL, PROJECT_BASED
    }

    public enum WorkQuality {
        HIGH, SATISFACTORY, NEEDS_IMPROVEMENT
    }

    public enum SessionRating {
        EXCELLENT, VERY_GOOD, AVERAGE, BELOW_EXPECTATIONS, POOR
    }
}
