package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.SessionFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Integer> {

    Page<SessionFeedback> findAll(Specification<SessionFeedback> specification, Pageable pageable);

}
