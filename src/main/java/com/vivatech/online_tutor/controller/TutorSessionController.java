package com.vivatech.online_tutor.controller;

import com.vivatech.online_tutor.dto.PaginationResponse;
import com.vivatech.online_tutor.helper.Constants;
import com.vivatech.online_tutor.webchat.dto.SessionRequestDTO;
import com.vivatech.online_tutor.webchat.dto.SessionResponseDTO;
import com.vivatech.online_tutor.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Create a new session")
    public SessionResponseDTO createSession(@Valid @RequestBody SessionRequestDTO requestDTO) {
        log.info("POST /api/v1/sessions - Creating new session");
        return sessionService.createSession(requestDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID")
    public SessionResponseDTO getSessionById(@PathVariable Integer id) {
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
            @RequestParam(required = false, defaultValue = "30") Integer size) {
        return sessionService.searchSessionsBySearchTerm(createdBy, searchTerm, pageNumber, size);
    }
}