package com.vivatech.onlinetutor.repository;


import com.vivatech.onlinetutor.model.MumlyTutorPayout;
import com.vivatech.onlinetutor.model.TutorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MumlyTutorPayoutRepository extends JpaRepository<MumlyTutorPayout, Integer> {
    MumlyTutorPayout findByTutorSessionId(Integer sessionId);
    MumlyTutorPayout findByTutorSessionIdAndPaymentStatusIn(Integer sessionId, List<String> paymentStatus);
    List<MumlyTutorPayout> findByTutorSessionIn(List<TutorSession> sessionList);

    List<MumlyTutorPayout> findByPaymentStatus(String paymentStatus);

    MumlyTutorPayout findByTutorSessionIdAndPaymentStatusNotIn(Integer sessionId, List<String> statusList);

    Integer countByTutorSessionInAndPaymentStatus(List<TutorSession> tutorSessions, String paymentStatus);

    List<MumlyTutorPayout> findByTutorSessionInAndPaymentStatus(List<TutorSession> tutorSessions, String paymentStatus);
}
