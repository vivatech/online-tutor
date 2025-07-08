package com.vivatech.onlinetutor.service;

import com.vivatech.onlinetutor.dto.DashboardFeedbackResponse;
import com.vivatech.onlinetutor.dto.DashboardResponse;
import com.vivatech.onlinetutor.dto.ParentFeedbackFilter;
import com.vivatech.onlinetutor.dto.SessionOverviewDto;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.model.MumlyTutorPayout;
import com.vivatech.onlinetutor.model.SessionFeedback;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.MumlyTutorPayoutRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import com.vivatech.onlinetutor.webchat.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class DashboardService {
    private final SessionRegistrationRepository sessionRegistrationRepository;
    private final MumlyTutorPayoutRepository mumlyTutorPayoutRepository;
    private final TutorSessionRepository tutorSessionRepository;
    private final SessionFeedbackService feedbackService;

    public DashboardService(TutorSessionRepository tutorSessionRepository,
                            MumlyTutorPayoutRepository mumlyTutorPayoutRepository,
                            SessionRegistrationRepository sessionRegistrationRepository, SessionFeedbackService feedbackService) {
        this.tutorSessionRepository = tutorSessionRepository;
        this.mumlyTutorPayoutRepository = mumlyTutorPayoutRepository;
        this.sessionRegistrationRepository = sessionRegistrationRepository;
        this.feedbackService = feedbackService;
    }

    public Integer findCompletedSession(User user) {
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
        return mumlyTutorPayoutRepository.countByTutorSessionInAndPaymentStatus(tutorSessions, AppEnums.PaymentStatus.SUCCESS.toString());
    }

    public Integer findInProgressSession(User user) {
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
        return mumlyTutorPayoutRepository.countByTutorSessionInAndPaymentStatus(tutorSessions, AppEnums.PaymentStatus.PENDING.toString());
    }

    public Double calculateGrossRevenue(User user) {
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
        List<MumlyTutorPayout> payoutList = mumlyTutorPayoutRepository.findByTutorSessionInAndPaymentStatus(tutorSessions, AppEnums.PaymentStatus.SUCCESS.toString());
        return payoutList.stream().mapToDouble(MumlyTutorPayout::getAmount).sum();
    }

    public Map<String, Double> revenueByMonth(User user) {
        int targetYear = Year.now().getValue();
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
        List<TutorSession> sessionList = tutorSessions.stream().filter(ele -> ele.getSessionDate().getYear() == targetYear).toList();
        List<MumlyTutorPayout> payoutList = mumlyTutorPayoutRepository.findByTutorSessionInAndPaymentStatus(sessionList, AppEnums.PaymentStatus.SUCCESS.toString());
        Map<String, Double> revenueByMonth = CustomUtils.initializeEmptyMonthMap();
        for (MumlyTutorPayout payout : payoutList) {
            double netAmount = payout.getNetAmount();
            String month = payout.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // "Jan", "Feb", etc.
            revenueByMonth.put(month, revenueByMonth.getOrDefault(month, 0.0) + netAmount);
        }
        return revenueByMonth;
    }

    public List<DashboardResponse.UpcomingSession> upcomingSessions(User user) {
        LocalDate oneMonthBefore = LocalDate.now().plusMonths(1);
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedByAndSessionDateGreaterThanEqual(user, oneMonthBefore);
        List<DashboardResponse.UpcomingSession> dtoList = new ArrayList<>();
        for (TutorSession tutorSession : tutorSessions) {
            DashboardResponse.UpcomingSession upcomingSession = new DashboardResponse.UpcomingSession();
            upcomingSession.setId(tutorSession.getId());
            upcomingSession.setTitle(tutorSession.getSessionTitle());
            String[] splitLearningObjective = tutorSession.getLearningObjectives().split(",");
            upcomingSession.setLessonCount(List.of(splitLearningObjective).size());
            upcomingSession.setDuration(tutorSession.getDurationMinutes());
            upcomingSession.setCreatedBy(tutorSession.getCreatedBy().getFullName());
            upcomingSession.setSubject(tutorSession.getSubject());
            upcomingSession.setSessionImage(tutorSession.getSessionCoverImageFile());
            dtoList.add(upcomingSession);
        }
        return dtoList;
    }

    public SessionOverviewDto sessionOverview(User user) {
        List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
        Integer registrationSize = sessionRegistrationRepository.countByRegisteredSessionIn(tutorSessions);
        List<MumlyTutorPayout> payoutList = mumlyTutorPayoutRepository.findByTutorSessionInAndPaymentStatus(tutorSessions, AppEnums.PaymentStatus.SUCCESS.toString());
        return new SessionOverviewDto(tutorSessions.size(), registrationSize, payoutList.stream().mapToDouble(MumlyTutorPayout::getNetAmount).sum());
    }

    public List<DashboardFeedbackResponse> latestFeedbacks(User user) {
        ParentFeedbackFilter dto = new ParentFeedbackFilter();
        dto.setTutorUsername(user.getUsername());
        dto.setPageNumber(0);
        dto.setSize(10);
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getSize());
        Page<SessionFeedback> feedbackFilter = feedbackService.filterEvent(dto, pageable);
        List<DashboardFeedbackResponse> dtoList = new ArrayList<>();
        feedbackFilter.getContent().forEach(ele -> {
            dtoList.add(new DashboardFeedbackResponse(ele.getParentName(), ele.getComment(), ele.getRating()));
        });
        return dtoList;
    }
}
