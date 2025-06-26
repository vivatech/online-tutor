package com.vivatech.onlinetutor.scheduler;

import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class OnlineTutorScheduler {

    @Autowired
    private TutorSessionRepository tutorSessionRepository;


    @Scheduled(cron = "0 0 0 * * *")
    private void updateUpcomingDates() {
        List<TutorSession> sessionList = tutorSessionRepository.findBySessionEndDateGreaterThanEqual(LocalDate.now());
        for (TutorSession tutorSession : sessionList) {
            if (tutorSession.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.WEEKLY)
                    || tutorSession.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.BIWEEKLY)) {
                tutorSession.getUpcomingDates().clear();
                tutorSession.setUpcomingDates(tutorSession.calculateUpcomingDates());
            }
            tutorSessionRepository.save(tutorSession);
        }
    }
}
