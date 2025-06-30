package com.vivatech.onlinetutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionOverviewDto {
    private Integer enrolledSession;
    private Integer totalStudents;
    private Double totalEarnings;
}
