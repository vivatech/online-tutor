package com.vivatech.onlinetutor.service;

import java.time.LocalDateTime;
import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.dto.SessionRegistrationDto;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.model.MumlyTutorPayment;
import com.vivatech.onlinetutor.model.TutorSession;

import com.vivatech.onlinetutor.dto.SessionRegistrationRequestDto;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.notification.OnlineTutorNotificationService;
import com.vivatech.onlinetutor.payment.PaymentDto;
import com.vivatech.onlinetutor.payment.PaymentService;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import com.vivatech.onlinetutor.webchat.dto.CreateUserRequest;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import com.vivatech.onlinetutor.webchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessionRegistrationService {
    
    @Autowired
    private SessionRegistrationRepository sessionRegistrationRepository;
    @Autowired
    private TutorSessionRepository tutorSessionRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OnlineTutorNotificationService notificationService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MumlyTutorPaymentRepository mumlyTutorPaymentRepository;

    @Transactional
    public Response createSessionRegistration(SessionRegistrationRequestDto requestDto) {

        SessionRegistration existingSessionRegistration = sessionRegistrationRepository.findByRegisteredSessionIdAndStudentPhone(requestDto.getRegistrationDto().getSessionId(), requestDto.getRegistrationDto().getStudentPhone());

        if (existingSessionRegistration != null) throw new OnlineTutorExceptionHandler("Student already registered for this session");

        SessionRegistrationDto dto = requestDto.getRegistrationDto();
        SessionRegistration sessionRegistration = new SessionRegistration();
        sessionRegistration.setStudentName(dto.getStudentName());
        sessionRegistration.setStudentPhone(dto.getStudentPhone());
        sessionRegistration.setStudentEmail(dto.getStudentEmail());
        sessionRegistration.setStudentAge(dto.getStudentAge());
        sessionRegistration.setGuardianName(dto.getGuardianName());
        sessionRegistration.setGuardianPhone(dto.getGuardianPhone());
        sessionRegistration.setGuardianEmail(dto.getGuardianEmail());

        TutorSession tutorSession = tutorSessionRepository.findById(dto.getSessionId()).orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found with ID: " + dto.getSessionId()));
        sessionRegistration.setRegisteredSession(tutorSession);
        sessionRegistration.setStatus(AppEnums.EventStatus.PENDING.toString());
        sessionRegistration.setCreatedAt(LocalDateTime.now());
        SessionRegistration savedRegistration = sessionRegistrationRepository.save(sessionRegistration);

        CreateUserRequest createParentUserRequest = prepareDtoToSaveParent(savedRegistration);
        CreateUserRequest createStudentUserRequest = prepareDtoToSaveStudent(savedRegistration);
        if (userRepository.findByUsername(createParentUserRequest.getUsername()).isEmpty()) userService.createUser(createParentUserRequest);
        if (userRepository.findByUsername(createStudentUserRequest.getUsername()).isEmpty()) userService.createUser(createStudentUserRequest);

        notificationService.sendAdminNotification(savedRegistration.getId(), AppEnums.NotificationType.REGISTRATION, null);
        requestDto.getPaymentDto().setSessionRegistrationId(savedRegistration.getId());
        Response response = paymentService.processPayment(requestDto.getPaymentDto());
        return Response.builder().status(AppEnums.EventStatus.SUCCESS.toString()).message("Session registration created successfully").data(response.getData()).build();
    }

    private CreateUserRequest prepareDtoToSaveParent(SessionRegistration sessionRegistration) {
        return CreateUserRequest.builder()
        		.username(sessionRegistration.getGuardianPhone())
        		.msisdn(sessionRegistration.getGuardianPhone())
        		.fullName(sessionRegistration.getGuardianName())
        		.email(sessionRegistration.getGuardianEmail())
        		.role(User.UserRole.PARENT)
        		.profilePicture(null)
        		.build();
    }

    private CreateUserRequest prepareDtoToSaveStudent(SessionRegistration sessionRegistration) {
        return CreateUserRequest.builder()
                .username(sessionRegistration.getStudentPhone())
                .msisdn(sessionRegistration.getStudentPhone())
                .fullName(sessionRegistration.getStudentName())
                .email(sessionRegistration.getStudentEmail())
                .role(User.UserRole.STUDENT)
                .profilePicture(null)
                .build();
    }

    public List<SessionRegistration> getAllSessionRegistrations() {
        return sessionRegistrationRepository.findAll();
    }

    public SessionRegistration getSessionRegistrationById(Integer id) {
        Optional<SessionRegistration> optionalRegistration = sessionRegistrationRepository.findById(id);
        return optionalRegistration.orElse(null);
    }

    public void deleteSessionRegistration(Integer id) {
        SessionRegistration registration = getSessionRegistrationById(id);
        if (registration != null) {
            sessionRegistrationRepository.delete(registration);
        }
    }

    public Response updateSessionRegistrationStatus(Integer id, AppEnums.EventStatus status) {
        SessionRegistration registration = getSessionRegistrationById(id);
        if (registration != null) {
            registration.setStatus(status.toString());
            sessionRegistrationRepository.save(registration);
            return Response.builder().status(AppEnums.EventStatus.SUCCESS.toString()).message("Session registration status updated successfully").build();
        }
        return Response.builder().status(AppEnums.EventStatus.FAILED.toString()).message("Session registration not found").build();
    }

    public PaginationResponse<SessionRegistration> getEnrolledSessionStudents(Integer sessionId, Integer pageNumber, Integer size) {
        List<SessionRegistration> registeredStudents = sessionRegistrationRepository.findByRegisteredSessionId(sessionId);
        Page<SessionRegistration> sessionRegistrations = CustomUtils.convertListToPage(registeredStudents, pageNumber, size);
        return CustomUtils.convertPageToPaginationResponse(sessionRegistrations, sessionRegistrations.getContent());
    }

    public void receiveCashPayment(String referenceNo) {
        MumlyTutorPayment payment = mumlyTutorPaymentRepository.findByReferenceNo(referenceNo);
        if (payment == null) throw new OnlineTutorExceptionHandler("Payment not found");
        String successTransactionId = CustomUtils.generateRandomString();
        paymentService.processPaymentCallBack(payment.getReferenceNo(), successTransactionId, AppEnums.PaymentStatus.COMPLETE.toString(), null);
    }

    public Response refundSessionRegistration(Integer id, String reason) {
        SessionRegistration sessionRegistration = sessionRegistrationRepository.findById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Session registration not found"));
        if (sessionRegistration.getStatus().equalsIgnoreCase(AppEnums.PaymentStatus.REFUND.toString())) throw new OnlineTutorExceptionHandler("Session registration already refunded");
        MumlyTutorPayment payment = mumlyTutorPaymentRepository.findBySessionRegistrationAndPaymentStatus(sessionRegistration, AppEnums.PaymentStatus.COMPLETE.toString());
        if (payment == null) throw new OnlineTutorExceptionHandler("Payment not found");
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setTransactionId(payment.getTransactionId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setReason(reason);
        return paymentService.refundTicket(paymentDto);
    }
}
