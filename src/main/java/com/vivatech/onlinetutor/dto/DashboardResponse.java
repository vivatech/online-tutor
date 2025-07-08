package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.model.TutorSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private Integer completedSessions;
    private Integer inProgressSessions;
    private Double grossRevenue;
    private Map<String, Double> revenueByMonth;
    private List<UpcomingSession> upcomingSessions;
    private SessionOverviewDto overview;
    private List<DashboardFeedbackResponse> feedbackResponses;

    @Data
    public static class UpcomingSession {
        private Integer id;
        private String title;
        private Integer lessonCount;
        private Integer duration;
        private String createdBy;
        private String subject;
        private String sessionImage;
    }
}
