package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.dto.SessionRegistrationRequestDto;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.service.SessionRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-registrations")
public class SessionRegistrationController {
    
    @Autowired
    private SessionRegistrationService sessionRegistrationService;

    // Create a new session registration
    @PostMapping
    public Response createSessionRegistration(@RequestBody SessionRegistrationRequestDto sessionRegistration) {
        return sessionRegistrationService.createSessionRegistration(sessionRegistration);
    }

    // Get all session registrations
    @GetMapping
    public ResponseEntity<List<SessionRegistration>> getAllSessionRegistrations() {
        List<SessionRegistration> registrations = sessionRegistrationService.getAllSessionRegistrations();
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    // Get session registration by ID
    @GetMapping("/{id}")
    public ResponseEntity<SessionRegistration> getSessionRegistrationById(@PathVariable Integer id) {
        SessionRegistration registration = sessionRegistrationService.getSessionRegistrationById(id);
        if (registration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }

    // Delete session registration
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionRegistration(@PathVariable Integer id) {
        sessionRegistrationService.deleteSessionRegistration(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/status/{status}")
    public Response updateSessionRegistrationStatus(@PathVariable Integer id, @PathVariable AppEnums.EventStatus status) {
        return sessionRegistrationService.updateSessionRegistrationStatus(id, status);
    }

    @GetMapping("/enrolled-students/{sessionId}")
    public PaginationResponse<SessionRegistration> getEnrolledSessionStudents(@PathVariable Integer sessionId,
                                                                              @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                              @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE) Integer pageSize) {
        return sessionRegistrationService.getEnrolledSessionStudents(sessionId, pageNumber, pageSize);
    }
}
