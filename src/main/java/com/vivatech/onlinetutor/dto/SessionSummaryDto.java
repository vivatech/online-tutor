package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.model.StudentFeedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionSummaryDto {
    @Data
    public static class SessionSummary {
        private String sessionName;
        private StudentFeedback.UnderstandingLevel rating;
    }
    private List<SessionSummary> sessionSummaries;
    private Map<StudentFeedback.UnderstandingLevel, Integer> summary;
}
