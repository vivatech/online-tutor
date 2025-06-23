package com.vivatech.onlinetutor.service;

import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.vivatech.onlinetutor.webchat.dto.SessionRequestDTO;
import com.vivatech.onlinetutor.webchat.dto.SessionResponseDTO;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {
    private final UserRepository userRepository;
    private final TutorSessionRepository tutorSessionRepository;

    public SessionResponseDTO createSession(SessionRequestDTO requestDTO) {
        log.info("Creating new session with title: {}", requestDTO.getSessionTitle());

        TutorSession session = mapToEntity(requestDTO);
        TutorSession savedSession = tutorSessionRepository.save(session);

        log.info("Successfully created session with ID: {}", savedSession.getId());
        return mapToResponseDTO(savedSession);
    }

    public SessionResponseDTO getSessionById(Integer id) {
        log.info("Fetching session with ID: {}", id);

        TutorSession session = tutorSessionRepository.findById(id)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found with ID: " + id));

        return mapToResponseDTO(session);
    }

    public List<SessionResponseDTO> getAllSessions(String userName, LocalDate viewDate) {

        LocalDate today = viewDate == null ? LocalDate.now() : viewDate;

        List<TutorSession> sessions = tutorSessionRepository
                .findByCreatedByAndSessionEndDateGreaterThanEqual(userRepository.findByUsername(userName)
                        .orElseThrow(() -> new OnlineTutorExceptionHandler("User not found")), today);
        // Step 1: filter the session by daily basic
        List<TutorSession> sessionList = sessions.stream()
                .filter(ele -> ele.getRecurrenceFrequency() == TutorSession.RecurrenceFrequency.DAILY)
                .collect(Collectors.toList());
        // Step 2: filter the session by occasional
        List<TutorSession> occasionalMeetings = sessions.stream()
                .filter(meeting -> meeting.getUpcomingDates().contains(today))
                .toList();
        // Step 3: Merge lists if occasional session has any elements
        if (!occasionalMeetings.isEmpty()) {
            if (sessionList.isEmpty()) sessionList = new ArrayList<>();
            sessionList.addAll(occasionalMeetings);
        }

        return sessionList.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteSession(Integer id) {
        log.info("Deleting session with ID: {}", id);

        if (!tutorSessionRepository.existsById(id)) {
            throw new OnlineTutorExceptionHandler("Session not found with ID: " + id);
        }

        tutorSessionRepository.deleteById(id);
        log.info("Successfully deleted session with ID: {}", id);
    }

    public PaginationResponse<SessionResponseDTO> searchSessionsBySearchTerm(String createdBy, String title, Integer pageNumber, Integer size) {
        log.info("Searching sessions by title: {}", title);

        List<TutorSession> sessions = tutorSessionRepository.searchAllSessions(title, createdBy);

        if (StringUtils.isEmpty(title))
            sessions = tutorSessionRepository
                    .findByCreatedBy(userRepository.findByUsername(createdBy)
                            .orElseThrow(() -> new OnlineTutorExceptionHandler("User not found")));

        Page<TutorSession> listToPage = CustomUtils.convertListToPage(sessions, pageNumber, size);
        List<SessionResponseDTO> dtoList = listToPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        PaginationResponse<SessionResponseDTO> response = new PaginationResponse<>();
        response.setContent(dtoList);
        response.setPage(listToPage.getNumber());
        response.setSize(listToPage.getSize());
        response.setTotalElements((int) listToPage.getTotalElements());
        response.setTotalPages(listToPage.getTotalPages());
        return response;
    }

    // Helper methods for mapping
    private TutorSession mapToEntity(SessionRequestDTO dto) {
        User createdBy = userRepository.findByUsername(dto.getCreatedBy()).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
        TutorSession session = null;
        if (dto.getId() != null) session = tutorSessionRepository.findById(dto.getId()).orElse(new TutorSession());
        else session = new TutorSession();
        session.setSessionTitle(dto.getSessionTitle());
        session.setSessionType(dto.getSessionType());
        session.setSessionDate(dto.getSessionDate());
        session.setSessionEndDate(dto.getSessionEndDate());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setTimeZone(dto.getTimeZone());
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setIsRecurring(dto.getIsRecurring());
        session.setSubject(dto.getSubject());
        session.setMaxStudents(dto.getMaxStudents());
        session.setMinEnrollment(dto.getMinEnrollment());
        session.setEnrollmentDeadline(dto.getEnrollmentDeadline());
        session.setLearningObjectives(String.join(",", dto.getLearningObjectives()));
        session.setTeachingMethods(String.join(",", dto.getTeachingMethods().stream().map(String::valueOf).distinct().toList()));
        session.setToolsRequired(String.join(",", dto.getToolsRequired().stream().map(String::valueOf).distinct().toList()));
        session.setLanguageOfInstruction(dto.getLanguageOfInstruction());
        session.setPricePerSession(dto.getPricePerSession());
        session.setDiscountCoupon(dto.getDiscountCoupon());
        session.setPaymentGatewayLinked(dto.getPaymentGatewayLinked());
        session.setAdminApprovalRequired(dto.getAdminApprovalRequired());
        session.setVisibility(dto.getVisibility());
        session.setCreatedBy(createdBy);
        session.setRecurrenceFrequency(dto.getRecurrenceFrequency());
        if (!session.getIsRecurring()) {
            if (dto.getUpcomingDates().isEmpty()) throw new OnlineTutorExceptionHandler("Upcoming dates are required for non-recurring sessions");
            session.getUpcomingDates().clear();
            session.setUpcomingDates(dto.getUpcomingDates());
        }
        if (dto.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.CUSTOM)) {
            if (!session.getUpcomingDates().isEmpty()) session.getUpcomingDates().clear();
            session.setUpcomingDates(dto.getUpcomingDates());
        } else if (dto.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.WEEKLY)) {
            session.setDayOfWeek(dto.getDayOfWeek());
            if (!session.getUpcomingDates().isEmpty()) session.getUpcomingDates().clear();
            session.setUpcomingDates(session.calculateUpcomingDates());
        } else if (dto.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.BIWEEKLY)) {
            session.setDayOfWeek(dto.getDayOfWeek());
            if (!session.getUpcomingDates().isEmpty()) session.getUpcomingDates().clear();
            session.setUpcomingDates(session.calculateUpcomingDates());
        }
        if (dto.getId() != null) session.setUpdatedBy(createdBy);
        log.info("Upcoming dates: {}", session.getUpcomingDates());
        return session;
    }

    private SessionResponseDTO mapToResponseDTO(TutorSession session) {
        SessionResponseDTO dto = new SessionResponseDTO();
        dto.setId(session.getId());
        dto.setSessionTitle(session.getSessionTitle());
        dto.setSessionType(session.getSessionType());
        dto.setSessionDate(session.getSessionDate());
        dto.setSessionEndDate(session.getSessionEndDate());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setTimeZone(session.getTimeZone());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setIsRecurring(session.getIsRecurring());
        dto.setRecurrenceFrequency(session.getRecurrenceFrequency());
        dto.setMaxStudents(session.getMaxStudents());
        dto.setMinEnrollment(session.getMinEnrollment());
        dto.setEnrollmentDeadline(session.getEnrollmentDeadline());

        dto.setLearningObjectives(new HashSet<>(Arrays.asList(session.getLearningObjectives().split(","))));
        dto.setTeachingMethods(Arrays.stream(session.getTeachingMethods().split(",")).map(TutorSession.TeachingMethod::valueOf).collect(Collectors.toSet()));
        dto.setToolsRequired(Arrays.stream(session.getToolsRequired().split(",")).map(TutorSession.ToolRequired::valueOf).collect(Collectors.toSet()));
        dto.setLanguageOfInstruction(session.getLanguageOfInstruction());
        dto.setPricePerSession(session.getPricePerSession());
        dto.setDiscountCoupon(session.getDiscountCoupon());
        dto.setPaymentGatewayLinked(session.getPaymentGatewayLinked());
        dto.setAdminApprovalRequired(session.getAdminApprovalRequired());
        dto.setVisibility(session.getVisibility());
        dto.setUpcomingDates(session.getUpcomingDates());
        if (session.getCreatedAt() != null) {
            dto.setCreatedAt(session.getCreatedAt());
        }
        if (session.getUpdatedAt() != null) {
            dto.setUpdatedAt(session.getUpdatedAt());
        }
        return dto;
    }
}