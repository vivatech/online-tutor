package com.vivatech.onlinetutor.service;


import com.vivatech.onlinetutor.dto.ParentFeedbackFilter;
import com.vivatech.onlinetutor.dto.ParentFeedbackRequest;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.model.SessionFeedback;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.SessionFeedbackRepository;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionFeedbackService {
    
    private final SessionFeedbackRepository sessionFeedbackRepository;
    private final TutorSessionRepository tutorSessionRepository;
    private final UserRepository userRepository;


    @Transactional
    public SessionFeedback createFeedback(ParentFeedbackRequest request) {
        TutorSession event = tutorSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found"));
        
        SessionFeedback feedback = new SessionFeedback();

        feedback.setParentName(request.getParentName());
        feedback.setRating(request.getRating());
        feedback.setTutorSession(event);
        feedback.setComment(request.getComment());
        if (request.getSubmittedById() != null) feedback.setSubmittedById(request.getSubmittedById());
        else {
            User mumlyAdmin = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
            if (mumlyAdmin != null) feedback.setSubmittedById(Math.toIntExact(mumlyAdmin.getId()));
        }
        feedback.setFeedbackDate(LocalDate.now());
        
        return sessionFeedbackRepository.save(feedback);
    }
    
    public Optional<SessionFeedback> getFeedbackById(Integer id) {
        return sessionFeedbackRepository.findById(id);
    }
    
    @Transactional
    public SessionFeedback updateFeedback(Integer id, ParentFeedbackRequest request) {
        SessionFeedback feedback = sessionFeedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        TutorSession tutorSession = tutorSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found"));
        
        feedback.setParentName(request.getParentName());
        feedback.setRating(request.getRating());
        feedback.setTutorSession(tutorSession);
        feedback.setComment(request.getComment());
        feedback.setSubmittedByType(request.getSubmittedByType().toString());
        if (request.getSubmittedById() != null) feedback.setSubmittedById(request.getSubmittedById());
        else {
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
            if (user != null) feedback.setSubmittedById(Math.toIntExact(user.getId()));
        }
        feedback.setFeedbackDate(LocalDate.now());
        
        return sessionFeedbackRepository.save(feedback);
    }
    
    @Transactional
    public void deleteFeedback(Integer id) {
        SessionFeedback feedback = sessionFeedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        sessionFeedbackRepository.delete(feedback);
    }

    public Page<SessionFeedback> filterEvent(ParentFeedbackFilter dto, Pageable pageable) {
        Specification<SessionFeedback> sessionFeedbackSpecification = getParentFeedbackSpecification(dto);
        return sessionFeedbackRepository.findAll(sessionFeedbackSpecification, pageable);
    }

    public Specification<SessionFeedback> getParentFeedbackSpecification(ParentFeedbackFilter dto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(dto.getSessionId())) {
                Join<SessionFeedback, TutorSession> sessionJoin = root.join("tutorSession");
                predicates.add(criteriaBuilder.equal(sessionJoin.get("id"), dto.getSessionId()));
            }

            if (dto.getStartDate() != null && dto.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("feedbackDate"), dto.getStartDate(), dto.getEndDate()));
            }

            if (dto.getSubmittedById() != null) {
                predicates.add(criteriaBuilder.equal(root.get("submittedById"), dto.getSubmittedById()));
            }

            if (!ObjectUtils.isEmpty(dto.getUsername())) {
                User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
                if (user != null) predicates.add(criteriaBuilder.equal(root.get("submittedById"), user.getId()));
            }

            if (!ObjectUtils.isEmpty(dto.getTutorUsername())) {
                User user = userRepository.findByUsername(dto.getTutorUsername()).orElseThrow(() -> new OnlineTutorExceptionHandler("Tutor not found"));
                if (user != null) {
                    List<TutorSession> tutorSessions = tutorSessionRepository.findByCreatedBy(user);
                    predicates.add(root.get("tutorSession").in(tutorSessions));
                }
            }

            query.orderBy(criteriaBuilder.desc(root.get("feedbackDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
