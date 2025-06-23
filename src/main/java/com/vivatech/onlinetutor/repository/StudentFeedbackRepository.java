package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.StudentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentFeedbackRepository extends JpaRepository<StudentFeedback, Integer> {

    Optional<StudentFeedback> findBySessionRegistrationId(Integer sessionRegistrationId);

    boolean existsBySessionRegistrationIdAndTutorId(Integer sessionRegistrationId, Long tutorId);
    
}