package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.StudentFeedbackRequest;
import com.vivatech.onlinetutor.dto.StudentFeedbackResponse;
import com.vivatech.onlinetutor.service.StudentFeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/student-feedback")
public class StudentFeedbackController {

    @Autowired
    private StudentFeedbackService studentFeedbackService;

    /**
     * Create new student feedback
     */
    @PostMapping
    public StudentFeedbackResponse createFeedback(@Valid @RequestBody StudentFeedbackRequest request) {
        return studentFeedbackService.createFeedback(request);
    }

    /**
     * Update existing student feedback
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<StudentFeedbackResponse> updateFeedback(
            @PathVariable Integer feedbackId,
            @Valid @RequestBody StudentFeedbackRequest request) {
        StudentFeedbackResponse response = studentFeedbackService.updateFeedback(feedbackId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get feedback by feedback ID
     */
    @GetMapping("/{feedbackId}")
    public ResponseEntity<StudentFeedbackResponse> getFeedbackById(@PathVariable Integer feedbackId) {
        try {
            StudentFeedbackResponse response = studentFeedbackService.getFeedbackById(feedbackId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/find-by-registration-id/{registrationId}")
    public StudentFeedbackResponse getFeedbackByRegistrationId(@PathVariable Integer registrationId) {
        return studentFeedbackService.getFeedbackByRegistrationId(registrationId);
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer feedbackId) {
        return studentFeedbackService.deleteFeedback(feedbackId);
    }
}