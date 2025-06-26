package com.vivatech.onlinetutor.controller;

import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.webchat.dto.SessionRequestDTO;
import com.vivatech.onlinetutor.webchat.dto.SessionResponseDTO;
import com.vivatech.onlinetutor.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
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
    public SessionResponseDTO createSession(@Valid @ModelAttribute SessionRequestDTO requestDTO) throws IOException {
        log.info("POST /api/v1/sessions - Creating new session");
        return sessionService.createSession(requestDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID")
    public TutorSession getSessionById(@PathVariable Integer id) {
        return sessionService.getSessionById(id);
    }


    @GetMapping
    @Operation(summary = "Get today's sessions for the tutor or sessions for a specific date")
    public List<SessionResponseDTO> getAllSessions(
            @RequestParam String userName,
            @RequestParam(required = false) LocalDate date) {
        return sessionService.getAllSessions(userName, date);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session created by the tutor")
    public void deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search sessions by title, type, subject and frequency")
    public PaginationResponse<SessionResponseDTO> searchSessionsByTitle(
            @RequestParam String createdBy,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE) Integer size) {
        return sessionService.searchSessionsBySearchTerm(createdBy, searchTerm, pageNumber, size);
    }

    @GetMapping("/view-session-by-phone-number/{phoneNumber}")
    @Operation(summary = "Search student sessions by providing phone number")
    public List<SessionResponseDTO> viewSessionByPhone(@PathVariable String phoneNumber) {
        return sessionService.findSessionListByPhoneNumber(phoneNumber);
    }
}