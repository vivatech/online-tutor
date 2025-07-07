package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.SessionSummaryDto;
import com.vivatech.onlinetutor.dto.StudentFeedbackRequest;
import com.vivatech.onlinetutor.dto.StudentFeedbackResponse;
import com.vivatech.onlinetutor.service.StudentFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/student-feedback")
@Tag(name = "Student Feedback", description = "CRUD APIs for managing student feedback")
public class StudentFeedbackController {

    @Autowired
    private StudentFeedbackService studentFeedbackService;

    /**
     * Create new student feedback
     */
    @Operation(summary = "Create new student feedback", description = "The online tutor will create this feedback for the student")
    @PostMapping
    public ResponseEntity<StudentFeedbackResponse> createFeedback(@Valid @RequestBody StudentFeedbackRequest request) {
        StudentFeedbackResponse feedback = studentFeedbackService.createFeedback(request);
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }

    /**
     * Update existing student feedback
     */
    @Operation(summary = "Update existing student feedback", description = "The online tutor will update this feedback for the student")
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
    @Operation(summary = "Get feedback by feedback ID",
            description = "The online tutor will get this feedback for the student by passing the feedback ID in path variable")
    @GetMapping("/{feedbackId}")
    public ResponseEntity<StudentFeedbackResponse> getFeedbackById(@PathVariable Integer feedbackId) {
        try {
            StudentFeedbackResponse response = studentFeedbackService.getFeedbackById(feedbackId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get feedback by registration ID",
            description = "The parent or student can view their feedback by passing session registration ID")
    @GetMapping("/find-by-registration-id/{registrationId}")
    public ResponseEntity<List<StudentFeedbackResponse>> getFeedbackByRegistrationId(@PathVariable Integer registrationId) {
        List<StudentFeedbackResponse> feedback = studentFeedbackService.getFeedbackByRegistrationId(registrationId);
        if (feedback.isEmpty()) return ResponseEntity.noContent().build();
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    @Operation(summary = "Get feedback summary by registration ID",
            description = "The parent or student can view their feedback summary chart by passing session registration ID")
    @GetMapping("/find-summary-by-registration-id/{registrationId}")
    public ResponseEntity<SessionSummaryDto> getFeedbackSummaryByRegistrationId(@PathVariable Integer registrationId) {
        SessionSummaryDto feedback = studentFeedbackService.getSessionSummariesByRegistrationId(registrationId);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    @Operation(summary = "Delete student feedback", description = "The online tutor will delete this feedback for the student")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer feedbackId) {
        return studentFeedbackService.deleteFeedback(feedbackId);
    }
}