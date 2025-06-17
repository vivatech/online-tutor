package com.vivatech.online_tutor.model;

import com.vivatech.online_tutor.webchat.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "mumly_tutor_session")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Session title is required")
    @Column(name = "session_title", nullable = false)
    private String sessionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @NotNull(message = "Session date is required")
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @NotNull(message = "Session end date is required")
    @Column(name = "session_end_date", nullable = false)
    private LocalDate sessionEndDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank(message = "Time zone is required")
    @Column(name = "time_zone", nullable = false)
    private String timeZone;

    @NotNull(message = "Duration is required")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Subject is required")
    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency")
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "min_enrollment")
    private Integer minEnrollment;

    @Column(name = "enrollment_deadline")
    private LocalDate enrollmentDeadline;

    @Column(name = "learning_objective")
    private String learningObjectives;

    @Column(name = "teaching_method")
    private String teachingMethods;

    @Column(name = "tool")
    private String toolsRequired;

    @Column(name = "language_instruction")
    private String languageOfInstruction;

    @Column(name = "price_per_session")
    private Double pricePerSession;

    @Column(name = "discount_coupon")
    private String discountCoupon;

    @Column(name = "payment_gateway_linked")
    private String paymentGatewayLinked;

    @Column(name = "admin_approval_required")
    private Boolean adminApprovalRequired = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private User updatedBy;

    private List<LocalDate> upcomingDates = new ArrayList<>();

    private List<String> dayOfWeek;

    // Enums
    public enum SessionType {
        ONE_ON_ONE,
        LIVE_VIRTUAL_GROUP_SESSION,
        LIVE_SINGLE_VIRTUAL
    }

    public enum RecurrenceFrequency {
        DAILY,
        WEEKLY,
        BIWEEKLY,
        CUSTOM
    }

    public enum TeachingMethod {
        LECTURE,
        INTERACTIVE_ACTIVITIES,
        ASSESSMENT_DRIVEN,
        DISCUSSION_BASED,
        PROBLEM_SOLVING,
        OTHER
    }

    public enum ToolRequired {
        DOCUMENT_SHARING,
        AUDIO_VIDEO,
        EXTERNAL_LINK
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

    public enum DAY_OF_WEEK {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public List<LocalDate> calculateUpcomingDates() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Calculate next occurrences
        for (int i = 0; i < 5; i++) {
            LocalDate nextDate;
            if (recurrenceFrequency == RecurrenceFrequency.WEEKLY) {
                // Find the next occurrence of the specified day of the week
                nextDate = today.plusWeeks(i);
                while (!nextDate.getDayOfWeek().toString().equalsIgnoreCase(dayOfWeek.stream().findFirst().orElse(null))) {
                    nextDate = nextDate.plusDays(1);
                }
                dates.add(nextDate);
            } else if (recurrenceFrequency == RecurrenceFrequency.BIWEEKLY) {
                // Calculate the next occurrence for the specified day of the month
                nextDate = today.plusWeeks(i);
                LocalDate finalNextDate = nextDate;
                while (dayOfWeek.stream().anyMatch(ele -> !finalNextDate.getDayOfWeek().toString().equalsIgnoreCase(ele))) {
                    nextDate = nextDate.plusDays(1);
                }
                dates.add(nextDate);
            }
        }

        return dates;
    }
}
