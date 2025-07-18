package com.vivatech.onlinetutor.controller;


import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.dto.ParentFeedbackFilter;
import com.vivatech.onlinetutor.dto.ParentFeedbackRequest;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.model.SessionFeedback;
import com.vivatech.onlinetutor.service.SessionFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tutor/session-feedback")
@Tag(name = "Session Feedback", description = "CRUD APIs for session feedback management which will be done by parents or students side.")
public class SessionFeedbackController {

    @Autowired
    private SessionFeedbackService sessionFeedbackService;

    @Operation(summary = "Create new session feedback",
            description = "The parent or student will create this feedback for the session")
    @PostMapping
    public ResponseEntity<SessionFeedback> saveSessionFeedback(@RequestBody ParentFeedbackRequest request) {
        return ResponseEntity.ok(sessionFeedbackService.createFeedback(request));
    }

    @Operation(summary = "Delete session feedback", description = "The parent or student can delete this feedback for the session")
    @DeleteMapping
    public ResponseEntity<String> deleteSessionFeedback(Integer id) {
        sessionFeedbackService.deleteFeedback(id);
        return ResponseEntity.ok("Feedback deleted.");
    }

    @Operation(summary = "Get session feedback by ID",
            description = "The parent or student can view their feedback by passing session feedback ID")
    @GetMapping("/{id}")
    public ResponseEntity<SessionFeedback> getSessionFeedback(Integer id) {
        SessionFeedback feedback = sessionFeedbackService.getFeedbackById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Feedback not found"));
        if (feedback == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(feedback);
    }

    @Operation(summary = "Get all session feedbacks",
            description = "The parent, student or tutor can view their feedback")
    @PostMapping("/filter")
    public ResponseEntity<PaginationResponse<SessionFeedback>> getAllFeedbacks(@RequestBody ParentFeedbackFilter dto) {
        int size = dto.getSize() != null ? dto.getSize() : Integer.parseInt(Constants.PAGE_SIZE);
        Pageable pageable = PageRequest.of(dto.getPageNumber() != null ? dto.getPageNumber() : 0, size);
        Page<SessionFeedback> feedbackFilter = sessionFeedbackService.filterEvent(dto, pageable);
        PaginationResponse<SessionFeedback> response = new PaginationResponse<>();
        response.setContent(feedbackFilter.getContent());
        response.setPage(feedbackFilter.getNumber());
        response.setSize(feedbackFilter.getSize());
        response.setTotalElements((int) feedbackFilter.getTotalElements());
        response.setTotalPages(feedbackFilter.getTotalPages());
        if (response.getContent().isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(response);
    }
}
