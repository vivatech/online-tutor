package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.SessionMeeting;
import com.vivatech.onlinetutor.model.TutorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionMeetingRepository extends JpaRepository<SessionMeeting, Integer> {
    SessionMeeting findByTutorSession(TutorSession tutorSession);
}
