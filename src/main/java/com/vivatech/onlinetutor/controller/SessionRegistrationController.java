package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.dto.SessionRegistrationRequestDto;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import com.vivatech.onlinetutor.service.SessionRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/session-registrations")
@Tag(name = "Session Registration", description = "APIs for managing session registrations")
public class SessionRegistrationController {
    
    @Autowired
    private SessionRegistrationService sessionRegistrationService;
    @Autowired
    private MumlyTutorPaymentRepository mumlyTutorPaymentRepository;

    // Create a new session registration
    @Operation(summary = "Create a new session registration")
    @PostMapping
    public ResponseEntity<String> createSessionRegistration(@RequestBody SessionRegistrationRequestDto sessionRegistration) {
        Response response = sessionRegistrationService.createSessionRegistration(sessionRegistration);
        if (response.getStatus().equalsIgnoreCase(AppEnums.EventStatus.FAILED.toString())) return new ResponseEntity<>(response.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response.getMessage(), HttpStatus.CREATED);
    }

    // Get all session registrations
    @Operation(summary = "Get all session registrations")
    @GetMapping
    @Transactional
    public List<SessionRegistration> getAllSessionRegistrations() {
        List<SessionRegistration> registrations = sessionRegistrationService.getAllSessionRegistrations();
        return registrations;
    }

    // Get session registration by ID
    @Operation(summary = "Get session registration by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SessionRegistration> getSessionRegistrationById(@PathVariable Integer id) {
        SessionRegistration registration = sessionRegistrationService.getSessionRegistrationById(id);
        if (registration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }

    // Delete session registration
    @Operation(summary = "Delete session registration")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionRegistration(@PathVariable Integer id) {
        sessionRegistrationService.deleteSessionRegistration(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update session registration status by passing registration ID and status")
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> updateSessionRegistrationStatus(@PathVariable Integer id, @PathVariable AppEnums.EventStatus status) {
        Response response = sessionRegistrationService.updateSessionRegistrationStatus(id, status);
        if (response.getStatus().equalsIgnoreCase(AppEnums.EventStatus.FAILED.toString())) return new ResponseEntity<>(response.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "Get enrolled session students by passing session ID")
    @GetMapping("/enrolled-students/{sessionId}")
    public ResponseEntity<PaginationResponse<SessionRegistration>> getEnrolledSessionStudents(@PathVariable Integer sessionId,
                                                                              @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                              @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE) Integer pageSize) {
        PaginationResponse<SessionRegistration> enrolledSessionStudents = sessionRegistrationService.getEnrolledSessionStudents(sessionId, pageNumber, pageSize);
        if (enrolledSessionStudents.getContent().isEmpty()) return ResponseEntity.noContent().build();
        return new ResponseEntity<>(enrolledSessionStudents, HttpStatus.OK);
    }

    @Operation(summary = "Receive cash payment")
    @GetMapping("/receive-cash-payment")
    public ResponseEntity<String> receiveCashPayment(@RequestParam String referenceNo) {
        sessionRegistrationService.receiveCashPayment(referenceNo);
        return ResponseEntity.ok("Payment received successfully");
    }

    @Operation(summary = "Refund session registration")
    @PostMapping("/refund")
    public ResponseEntity<Response> refundTicket(@RequestParam Integer participantId, @RequestParam String reason) {
        return new ResponseEntity<>(sessionRegistrationService.refundSessionRegistration(participantId, reason), HttpStatus.OK);
    }
}
