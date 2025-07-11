package com.vivatech.onlinetutor.service;

import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.dto.PayoutRequestDto;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.model.MumlyTutorPayout;
import com.vivatech.onlinetutor.model.SessionMeeting;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.repository.MumlyTutorPayoutRepository;
import com.vivatech.onlinetutor.repository.SessionMeetingRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import com.vivatech.onlinetutor.videochat.MeetingResponseDto;
import com.vivatech.onlinetutor.videochat.VideoChatDto;
import com.vivatech.onlinetutor.videochat.VideoChatProcessor;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.vivatech.onlinetutor.webchat.dto.SessionRequestDTO;
import com.vivatech.onlinetutor.webchat.dto.SessionResponseDTO;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {
    private final MumlyTutorPayoutRepository mumlyTutorPayoutRepository;
    private final SessionRegistrationRepository sessionRegistrationRepository;
    private final SessionMeetingRepository sessionMeetingRepository;
    private final UserRepository userRepository;
    private final TutorSessionRepository tutorSessionRepository;
    private final VideoChatProcessor videoChatProcessor;
    private final FileStorageService fileStorageService;

    public SessionResponseDTO createSession(SessionRequestDTO requestDTO) throws IOException {
        log.info("Creating new session with title: {}", requestDTO.getSessionTitle());

        TutorSession session = mapToEntity(requestDTO);
        // Save uploaded files
        if (requestDTO.getSessionCoverImageFile() != null && session.getSessionCoverImageFile() == null) {
            String extension = fileStorageService.getFileExtension(Objects.requireNonNull(requestDTO.getSessionCoverImageFile().getOriginalFilename()));
            String fileName = UUID.randomUUID() + "." + extension;
            session.setSessionCoverImageFile(fileName);
        }
        TutorSession savedSession = tutorSessionRepository.save(session);
        if (requestDTO.getSessionCoverImageFile() != null) {
            fileStorageService.storeFile(requestDTO.getSessionCoverImageFile(), session.getSessionCoverImageFile(), "");
        }
        if (requestDTO.getTeachingMaterialFile() != null) {
            fileStorageService.storeFile(requestDTO.getTeachingMaterialFile(), savedSession.getTeachingMaterial(), "");
        }
        createSessionMeeting(savedSession);

        log.info("Successfully created session with ID: {}", savedSession.getId());
        return mapToResponseDTO(savedSession);
    }

    private TutorSession handleTeachingMaterialFIleSave(TutorSession session, SessionRequestDTO requestDTO) {
        if (session.getId() != null) {
            // Updating existing session
            if (requestDTO.getDocumentType().equals(AppEnums.DocumentType.LINK)) {
                // If previous was file, delete it
                if (!StringUtils.isEmpty(session.getTeachingMaterial())
                        && session.getTeachingMaterialType().equals(AppEnums.DocumentType.FILE.toString())) {
                    fileStorageService.deleteFile("", session.getTeachingMaterial());
                }
                session.setTeachingMaterialType(AppEnums.DocumentType.LINK.toString());
                session.setTeachingMaterial(requestDTO.getTeachingMaterialLink());

            } else if (requestDTO.getDocumentType().equals(AppEnums.DocumentType.FILE)) {
                // If previous was link, no need to delete anything
                // Replace old file with new one if needed
                if (!StringUtils.isEmpty(session.getTeachingMaterial())
                        && session.getTeachingMaterialType().equals(AppEnums.DocumentType.FILE.toString())) {
                    fileStorageService.deleteFile("", session.getTeachingMaterial());
                }

                String extension = fileStorageService.getFileExtension(
                        Objects.requireNonNull(requestDTO.getTeachingMaterialFile().getOriginalFilename()));
                String referenceNumber = UUID.randomUUID() + "." + extension;

                session.setTeachingMaterialType(AppEnums.DocumentType.FILE.toString());
                session.setTeachingMaterial(referenceNumber);
            }

        } else {
            // Creating new session
            if (requestDTO.getDocumentType().equals(AppEnums.DocumentType.FILE)) {
                String extension = fileStorageService.getFileExtension(
                        Objects.requireNonNull(requestDTO.getTeachingMaterialFile().getOriginalFilename()));
                String referenceNumber = UUID.randomUUID() + "." + extension;

                session.setTeachingMaterialType(AppEnums.DocumentType.FILE.toString());
                session.setTeachingMaterial(referenceNumber);
            } else {
                session.setTeachingMaterialType(AppEnums.DocumentType.LINK.toString());
                session.setTeachingMaterial(requestDTO.getTeachingMaterialLink());
            }
        }
        return session;
    }

    public TutorSession getSessionById(Integer id) {

        return tutorSessionRepository.findById(id)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found with ID: " + id));
    }

    public List<SessionResponseDTO> getAllSessions(String userName, LocalDate viewDate, Boolean displayAll, String subject, String sessionName) {

        LocalDate today = viewDate == null ? LocalDate.now() : viewDate;

        List<TutorSession> sessions = tutorSessionRepository
                .findByCreatedByAndSessionEndDateGreaterThanEqual(userRepository.findByUsername(userName)
                        .orElseThrow(() -> new OnlineTutorExceptionHandler("User not found")), today);
        if (displayAll && viewDate != null) throw new OnlineTutorExceptionHandler("Cannot display all sessions for a specific date");
        if (displayAll) return sessions.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        if (!StringUtils.isEmpty(subject)) sessions = sessions.stream().filter(ele -> ele.getSubject().equalsIgnoreCase(subject)).collect(Collectors.toList());
        if (!StringUtils.isEmpty(sessionName)) sessions = sessions.stream().filter(ele -> ele.getSessionTitle().equalsIgnoreCase(sessionName)).collect(Collectors.toList());
        return getUpcomingMeeting(sessions, today);

    }

    private List<SessionResponseDTO> getUpcomingMeeting(List<TutorSession> sessions, LocalDate today) {
        // Step 1: filter the session by daily basic
        List<TutorSession> sessionList = sessions.stream()
                .filter(ele -> ele.getRecurrenceFrequency() == TutorSession.RecurrenceFrequency.DAILY)
                .collect(Collectors.toList());
        // Step 2: filter the session by occasional
        List<TutorSession> occasionalMeetings = sessions.stream()
                .filter(meeting -> meeting.getUpcomingDates() != null)
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
        TutorSession tutorSession = tutorSessionRepository.findById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found with ID: " + id));
        SessionMeeting sessionMeeting = sessionMeetingRepository.findByTutorSession(tutorSession);
        if (sessionMeeting != null) {
            VideoChatDto videoChatDto = new VideoChatDto();
            videoChatDto.setMeetingId(sessionMeeting.getMeetingId());
            videoChatProcessor.deleteMeeting(videoChatDto, AppEnums.MeetingAggregator.ZOOM);
            sessionMeetingRepository.delete(sessionMeeting);
        }
        fileStorageService.deleteFile("", tutorSession.getSessionCoverImageFile());
        tutorSessionRepository.delete(tutorSession);
        log.info("Successfully deleted session with ID: {}", id);
    }

    public PaginationResponse<SessionResponseDTO> searchSessionsBySearchTerm(String createdBy, String title, String subject, Integer pageNumber, Integer size) {
        log.info("Searching sessions by title: {}", title);

        if (pageNumber == null) pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<TutorSession> sessions = tutorSessionRepository.findAll(getSessionSearchSpecification(title, subject, createdBy), pageable);

        List<SessionResponseDTO> dtoList = sessions.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        PaginationResponse<SessionResponseDTO> response = new PaginationResponse<>();
        response.setContent(dtoList);
        response.setPage(sessions.getNumber());
        response.setSize(sessions.getSize());
        response.setTotalElements((int) sessions.getTotalElements());
        response.setTotalPages(sessions.getTotalPages());
        return response;
    }

    public Specification<TutorSession> getSessionSearchSpecification(String title, String subject, String createdBy) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(title)) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("sessionTitle")), "%" + title.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("sessionType")), "%" + title.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("recurrenceFrequency")), "%" + title.toLowerCase() + "%")
                ));
            }
            if (!StringUtils.isEmpty(subject)) {
                predicates.add(cb.like(root.get("subject"), "%" + subject + "%"));
            }
            if (!StringUtils.isEmpty(createdBy)) {
                User user = userRepository.findByUsername(createdBy).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
                predicates.add(cb.equal(root.get("createdBy"), user));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
        session.setDurationMinutes(dto.getDuration());
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
        if (dto.getAdminApprovalRequired()) session.setStatus(AppEnums.EventStatus.PENDING.toString());
        else session.setStatus(AppEnums.EventStatus.ACTIVE.toString());
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
        //Handle file and link for Teaching material
        return handleTeachingMaterialFIleSave(session, dto);
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
        dto.setSessionImage(session.getSessionCoverImageFile());
        dto.setSubject(session.getSubject());
        dto.setSessionStatus(session.getStatus());

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
        SessionMeeting sessionMeeting = sessionMeetingRepository.findByTutorSession(session);
        if (sessionMeeting != null) {
            MeetingResponseDto meetingResponseDto = MeetingResponseDto.builder()
                    .meetingId(sessionMeeting.getMeetingId())
                    .hostUrl(sessionMeeting.getHostUrl())
                    .joinUrl(sessionMeeting.getJoinUrl())
                    .build();
            dto.setMeetingDto(meetingResponseDto);
        }
        return dto;
    }

    private void createSessionMeeting(TutorSession savedSession) {
        SessionMeeting sessionMeeting = sessionMeetingRepository.findByTutorSession(savedSession);
        if (sessionMeeting != null) return;
        VideoChatDto videoChatDto = VideoChatDto.builder()
                .meetingTitle(savedSession.getSessionTitle())
                .meetingStartDate(savedSession.getSessionDate())
                .meetingStartTime(savedSession.getStartTime().toString())
                .meetingDuration(savedSession.getDurationMinutes())
                .createdBy(savedSession.getCreatedBy().getUsername())
                .isRecurring(savedSession.getIsRecurring())
                .build();
        if (savedSession.getIsRecurring()) {
            videoChatDto.setMeetingEndDate(savedSession.getSessionEndDate());
            videoChatDto.setMeetingEndTime(savedSession.getEndTime().toString());
        }
        Response meetingResponse = videoChatProcessor.createMeeting(videoChatDto, AppEnums.MeetingAggregator.ZOOM);
        MeetingResponseDto dto = (MeetingResponseDto) meetingResponse.getData();
        SessionMeeting meeting = new SessionMeeting();
        meeting.setTutorSession(savedSession);
        meeting.setHostUrl(dto.getHostUrl());
        meeting.setJoinUrl(dto.getJoinUrl());
        meeting.setMeetingId(dto.getMeetingId());
        sessionMeetingRepository.save(meeting);
    }

    public List<SessionResponseDTO> findSessionListByPhoneNumber(String phoneNumber) {
        List<SessionRegistration> registrationList = sessionRegistrationRepository.findByStudentPhoneContaining(phoneNumber);
        List<TutorSession> tutorSessions = registrationList.stream().map(SessionRegistration::getRegisteredSession).toList();
        return getUpcomingMeeting(tutorSessions, null);
    }

    @Transactional
    public Response savePayoutDetail(PayoutRequestDto dto) {
        MumlyTutorPayout mumlyEventPayout = mumlyTutorPayoutRepository.findByTutorSessionId(dto.getSessionId());
        if (mumlyEventPayout == null) throw new OnlineTutorExceptionHandler("Payout not found");
        mumlyEventPayout.setCommission(dto.getCommission());
        mumlyEventPayout.setNetAmount(dto.getNetAmount());
        mumlyEventPayout.setPaymentStatus(dto.getPaymentStatus().toString());
        mumlyEventPayout.setTransactionId(dto.getTransactionId());
        mumlyEventPayout.setReferenceNo(dto.getReferenceNo());
        mumlyEventPayout.setPaymentMode(dto.getPaymentMode().toString());
        mumlyEventPayout.setReason(dto.getReason());
        mumlyTutorPayoutRepository.save(mumlyEventPayout);
        return Response.builder().status(AppEnums.EventStatus.SUCCESS.toString()).message("Payout updated successfully.").build();
    }

    public List<PayoutRequestDto> getPendingPayouts() {
        List<MumlyTutorPayout> tutorPayouts = mumlyTutorPayoutRepository.findByPaymentStatus(AppEnums.PaymentStatus.PENDING.toString());
        List<PayoutRequestDto> dtoList = new ArrayList<>();
        for (MumlyTutorPayout tutorPayout : tutorPayouts) {
            PayoutRequestDto dto = new PayoutRequestDto();
            dto.setSessionId(tutorPayout.getTutorSession().getId());
            dto.setAmount(tutorPayout.getAmount());
            dto.setCommission(null);
            dto.setNetAmount(null);
            dto.setPaymentStatus(AppEnums.PaymentStatus.valueOf(tutorPayout.getPaymentStatus()));
            dtoList.add(dto);
        }
        return dtoList;
    }
}