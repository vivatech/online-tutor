package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.DashboardResponse;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.service.DashboardService;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tutor/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}")
    public DashboardResponse dashboard(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found."));
        return DashboardResponse.builder()
                .completedSessions(dashboardService.findCompletedSession(user))
                .inProgressSessions(dashboardService.findInProgressSession(user))
                .grossRevenue(dashboardService.calculateGrossRevenue(user))
                .revenueByMonth(dashboardService.revenueByMonth(user))
                .upcomingSessions(dashboardService.upcomingSessions(user))
                .overview(dashboardService.sessionOverview(user))
                .build();
    }
}
