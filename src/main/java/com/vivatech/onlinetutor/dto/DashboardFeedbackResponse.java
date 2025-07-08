package com.vivatech.onlinetutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardFeedbackResponse {
    private String parentName;
    private String feedback;
    private Integer rating;
}
