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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-registrations")
@Tag(name = "Session Registration", description = "APIs for managing session registrations")
public class SessionRegistrationController {
    
    @Autowired
    private SessionRegistrationService sessionRegistrationService;
    @Autowired
    private MumlyTutorPaymentRepository mumlyTutorPaymentRepository;

    // Create a new session registration
    @Operation(summary = "Create a new session registration")
    @PostMapping
    public Response createSessionRegistration(@RequestBody SessionRegistrationRequestDto sessionRegistration) {
        return sessionRegistrationService.createSessionRegistration(sessionRegistration);
    }

    // Get all session registrations
    @Operation(summary = "Get all session registrations")
    @GetMapping
    public ResponseEntity<List<SessionRegistration>> getAllSessionRegistrations() {
        List<SessionRegistration> registrations = sessionRegistrationService.getAllSessionRegistrations();
        return new ResponseEntity<>(registrations, HttpStatus.OK);
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
    public Response updateSessionRegistrationStatus(@PathVariable Integer id, @PathVariable AppEnums.EventStatus status) {
        return sessionRegistrationService.updateSessionRegistrationStatus(id, status);
    }

    @Operation(summary = "Get enrolled session students by passing session ID")
    @GetMapping("/enrolled-students/{sessionId}")
    public PaginationResponse<SessionRegistration> getEnrolledSessionStudents(@PathVariable Integer sessionId,
                                                                              @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                              @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE) Integer pageSize) {
        return sessionRegistrationService.getEnrolledSessionStudents(sessionId, pageNumber, pageSize);
    }

    @Operation(summary = "Receive cash payment")
    @GetMapping("/receive-cash-payment")
    public Response receiveCashPayment(@RequestParam String referenceNo) {
        sessionRegistrationService.receiveCashPayment(referenceNo);
        return Response.builder().status(AppEnums.PaymentStatus.SUCCESS.toString()).message("Payment received successfully").build();
    }
}
