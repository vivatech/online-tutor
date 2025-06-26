package com.vivatech.onlinetutor.repository;


import com.vivatech.onlinetutor.model.Attendance;
import com.vivatech.onlinetutor.model.SessionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("SELECT a.date, " +
            "SUM(CASE WHEN a.present = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.present = false THEN 1 ELSE 0 END) " +
            "FROM Attendance a " +
            "WHERE a.sessionRegistration.id = ?1 " +
            "AND a.date BETWEEN ?2 AND ?3 " +
            "GROUP BY a.date")
    List<Object[]> getAttendanceSummary(Integer participantId, LocalDate startDate, LocalDate endDate);


    List<Attendance> findBySessionRegistrationId(Integer participantId);

    Attendance findBySessionRegistrationIdAndDate(Integer participantId, LocalDate date);

    List<Attendance> findBySessionRegistrationIn(List<SessionRegistration> registrations);

    @Query("SELECT a.date, " +
            "SUM(CASE WHEN a.present = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.present = false THEN 1 ELSE 0 END), " +
            "a.sessionRegistration.studentName, a.sessionRegistration.studentEmail " +
            "FROM Attendance a " +
            "WHERE a.sessionRegistration.registeredSession.id = ?1 " +
            "AND a.date BETWEEN ?2 AND ?3 " +
            "GROUP BY a.date")
    List<Object[]> getAttendanceListByEvent(Integer sessionId, LocalDate startDate, LocalDate endDate);
}
