package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.*;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.webchat.dto.SessionRequestDTO;
import com.vivatech.onlinetutor.webchat.dto.SessionResponseDTO;
import com.vivatech.onlinetutor.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session Management", description = "APIs for managing and viewing sessions created by the tutor")
public class TutorSessionController {

    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Create a new session",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SessionRequestDTO.class) // Link to your DTO schema
                    )
            )
    )
    public ResponseEntity<SessionResponseDTO> createSession(@Valid @ModelAttribute SessionRequestDTO requestDTO) throws IOException {
        log.info("POST /api/v1/sessions - Creating new session");
        return ResponseEntity.ok(sessionService.createSession(requestDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<TutorSession> getSessionById(@PathVariable Integer id) {
        TutorSession session = sessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }


    @GetMapping
    @Operation(summary = "Get today's sessions for the tutor or sessions for a specific date",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully received sessions",
                            content = @Content(schema = @Schema(implementation = SessionResponseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No sessions found")
            }
    )
    public ResponseEntity<List<SessionResponseDTO>> getAllSessions(
            @RequestParam String userName,
            @RequestParam(required = false, defaultValue = "false") Boolean displayAll,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String sessionName) {
        List<SessionResponseDTO> sessions = sessionService.getAllSessions(userName, date, displayAll, subject, sessionName);
        if (sessions.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session created by the tutor")
    public void deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search sessions by title, type, subject, price and frequency")
    public ResponseEntity<PaginationResponse<SessionResponseDTO>> searchSessionsByTitle(
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "0") Double startPrice,
            @RequestParam(required = false, defaultValue = "0") Double endPrice,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE) Integer size) {
        PaginationResponse<SessionResponseDTO> paginationResponse = sessionService.searchSessionsBySearchTerm(createdBy, searchTerm, subject, startTime, endTime, startPrice, endPrice, pageNumber, size);
        if (paginationResponse.getContent().isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(paginationResponse);
    }

    @GetMapping("/view-session-by-phone-number/{phoneNumber}")
    @Operation(summary = "Search student sessions by providing phone number")
    public ResponseEntity<List<SessionResponseDTO>> viewSessionByPhone(@PathVariable String phoneNumber) {
        List<SessionResponseDTO> listByPhoneNumber = sessionService.findSessionListByPhoneNumber(phoneNumber);
        if (listByPhoneNumber.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(listByPhoneNumber);
    }

    @Operation(summary = "Send payout to tutor for particular session",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = PayoutRequestDto.class) // Link to your DTO schema
                    )
            )
    )
    @PostMapping("/send-payout-of-tutor")
    public ResponseEntity<String> sendPayoutOfTutor(@ModelAttribute PayoutRequestDto dto) {
        Response response = sessionService.savePayoutDetail(dto);
        if (response.getStatus().equalsIgnoreCase(AppEnums.EventStatus.FAILED.toString())) return new ResponseEntity<>(response.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "Get all pending payouts which has to be paid by Super admin")
    @GetMapping("/get-pending-payouts")
    public ResponseEntity<List<PayoutResponseDto>> getPendingPayouts() {
        List<PayoutResponseDto> pendingPayouts = sessionService.getPendingPayouts();
        if (pendingPayouts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(pendingPayouts);
    }

    @Operation(summary = "Get registered participants list for payment")
    @GetMapping("/get-session-by-id-for-payment/{id}")
    public ResponseEntity<List<PaymentSessionRegistrationResponseDto>> getSessionByIdForPayment(@PathVariable Integer id) {
        List<PaymentSessionRegistrationResponseDto> paymentSessionRegistration = sessionService.getPaymentSessionRegistration(id);
        if (paymentSessionRegistration.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(paymentSessionRegistration);
    }

    @Operation(summary = "Get all subjects of the session created the tutor")
    @GetMapping("/tutor-subjects")
    public ResponseEntity<List<String>> getTutorSubjects(@RequestParam(required = false) String subjectName) {
        List<String> subjects = sessionService.getTutorSubjects(subjectName);
        if (subjects.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(subjects);
    }


}