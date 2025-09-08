package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.model.TutorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRegistrationRepository extends JpaRepository<SessionRegistration, Integer> {
    List<SessionRegistration> findByStatus(String status);

    List<SessionRegistration> findByStudentEmail(String email);

    List<SessionRegistration> findByRegisteredSessionId(Integer sessionId);

    List<SessionRegistration> findByRegisteredSessionIdAndStatusNotIn(Integer sessionId, List<String> statusList);

    Integer countByRegisteredSessionIdAndStatusNotIn(Integer sessionId, List<String> statusList);

    Integer countByRegisteredSessionIn(List<TutorSession> sessionId);

    SessionRegistration findByRegisteredSessionIdAndStudentPhone(Integer sessionId, String studentPhone);

    List<SessionRegistration> findByStudentPhoneContaining(String phoneNumber);
}
