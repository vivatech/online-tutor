package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.StudentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StudentFeedbackRepository extends JpaRepository<StudentFeedback, Integer> {

    List<StudentFeedback> findBySessionRegistrationId(Integer sessionRegistrationId);

    boolean existsBySessionRegistrationIdAndTutorIdAndCreatedAt(Integer sessionRegistrationId, Long tutorId, LocalDate localDate);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM StudentFeedback e " +
            "WHERE e.sessionRegistration.id = :sessionRegistrationId " +
            "AND e.tutor.id = :tutorId " +
            "AND e.createdAt BETWEEN :startOfDay AND :endOfDay")
    boolean existsBySessionRegistrationIdAndTutorIdAndCreatedAtDate(
            @Param("sessionRegistrationId") Integer sessionRegistrationId,
            @Param("tutorId") Long tutorId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );


}