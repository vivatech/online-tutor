package com.vivatech.onlinetutor.notification;

import java.time.LocalDateTime;
import java.util.List;
import com.vivatech.mumly_event.notification.repository.AdminNotificationRepository;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.model.MumlyTutorPayment;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vivatech.mumly_event.notification.model.AdminNotification;

@Service
@Slf4j
public class OnlineTutorNotificationService {
    @Autowired
    private TutorSessionRepository tutorSessionRepository;
    @Autowired
    private MumlyTutorPaymentRepository mumlyTutorPaymentRepository;
    @Autowired
    private AdminNotificationRepository adminNotificationRepository;
    @Autowired
    private SessionRegistrationRepository sessionRegistrationRepository;

    public void sendAdminNotification(Integer sessionId, AppEnums.NotificationType type, String message) {
        log.info("Sending admin notification for session: {}", sessionId);
        if (type.equals(AppEnums.NotificationType.REGISTRATION)) {
            createRegistrationNotification(sessionId);
        } else if (type.equals(AppEnums.NotificationType.PAYMENT)) {
            createPaymentNotification(sessionId);
        } else if (type.equals(AppEnums.NotificationType.EMERGENCY)) {
            sendEmergencyNotification(sessionId, message);
        }

    }

    public void createRegistrationNotification(Integer sessionId) {
        SessionRegistration sessionRegistration = sessionRegistrationRepository.findById(sessionId)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session not found with ID: " + sessionId));
        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setMessage(sessionRegistration.getStudentName()
                + " has registered for "
                + sessionRegistration.getRegisteredSession().getSessionTitle());
        adminNotification.setType(AppEnums.NotificationType.REGISTRATION.toString());
        adminNotification.setRead(false);
        adminNotification.setSenderMsisdn(sessionRegistration.getStudentPhone());
        adminNotification.setReceiverMsisdn(sessionRegistration.getRegisteredSession().getCreatedBy().getMsisdn());
        adminNotification.setSenderEmil(sessionRegistration.getStudentEmail());
        adminNotification.setReceiverEmail(sessionRegistration.getRegisteredSession().getCreatedBy().getEmail());
        adminNotification.setEmailSentStatus(AppEnums.EventStatus.PENDING.toString());
        adminNotification.setSmsSentStatus(AppEnums.EventStatus.PENDING.toString());
        adminNotification.setRetryCount(0);
        adminNotification.setCreatedAt(LocalDateTime.now());
        adminNotification.setOrganizerId(Math.toIntExact(sessionRegistration.getRegisteredSession().getCreatedBy().getId()));
        adminNotification.setApplicationName(AppEnums.ApplicationName.MUMLY_TUTOR.toString());
        adminNotification.setOwnerName(sessionRegistration.getRegisteredSession().getCreatedBy().getFullName());

        adminNotificationRepository.save(adminNotification);
    }

    public void createPaymentNotification(Integer paymentId) {
        MumlyTutorPayment mumlyTutorPayment = mumlyTutorPaymentRepository.findById(paymentId).orElseThrow(() -> new com.vivatech.mumly_event.exception.CustomExceptionHandler("Payment not found"));
        SessionRegistration sessionRegistration = mumlyTutorPayment.getSessionRegistration();
        if (mumlyTutorPayment.getSessionRegistration().getStudentName() == null) {
            sessionRegistration = sessionRegistrationRepository.findById(mumlyTutorPayment.getSessionRegistration().getId())
                    .orElseThrow(() -> new com.vivatech.mumly_event.exception.CustomExceptionHandler("Event registration not found"));
        }
        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setMessage(sessionRegistration.getStudentName()
                + " has paid for " + sessionRegistration.getRegisteredSession().getSessionTitle() + "."
                + " Payment status: " + mumlyTutorPayment.getPaymentStatus());
        adminNotification.setRead(false);
        adminNotification.setType(AppEnums.NotificationType.PAYMENT.toString());
        adminNotification.setOrganizerId(Math.toIntExact(sessionRegistration.getRegisteredSession().getCreatedBy().getId()));
        adminNotification.setSenderMsisdn(sessionRegistration.getStudentPhone());
        adminNotification.setReceiverMsisdn(sessionRegistration.getRegisteredSession().getCreatedBy().getMsisdn());
        adminNotification.setSenderEmil(sessionRegistration.getRegisteredSession().getCreatedBy().getEmail());
        adminNotification.setReceiverEmail(sessionRegistration.getRegisteredSession().getCreatedBy().getEmail());
        adminNotification.setEmailSentStatus(AppEnums.EventStatus.PENDING.toString());
        adminNotification.setSmsSentStatus(AppEnums.EventStatus.PENDING.toString());
        adminNotification.setRetryCount(0);
        adminNotification.setCreatedAt(LocalDateTime.now());
        adminNotification.setApplicationName(AppEnums.ApplicationName.MUMLY_TUTOR.toString());
        adminNotification.setOwnerName(sessionRegistration.getRegisteredSession().getCreatedBy().getFullName());
        adminNotificationRepository.save(adminNotification);
    }

    public void sendEmergencyNotification(Integer sessionId, String message) {
        List<SessionRegistration> participants = sessionRegistrationRepository.findByRegisteredSessionId(sessionId)
                .stream()
                .filter(ele -> ele.getStatus().equalsIgnoreCase(AppEnums.EventStatus.APPROVE.toString()))
                .toList();
        for (SessionRegistration participant : participants) {
            AdminNotification notification = new AdminNotification();
            notification.setMessage(message);
            notification.setType(AppEnums.NotificationType.EMERGENCY.toString());
            notification.setRead(false);
            notification.setSenderMsisdn(participant.getRegisteredSession().getCreatedBy().getMsisdn());
            notification.setReceiverMsisdn(participant.getStudentPhone());
            notification.setSenderEmil(participant.getRegisteredSession().getCreatedBy().getEmail());
            notification.setReceiverEmail(participant.getStudentEmail());
            notification.setEmailSentStatus(AppEnums.EventStatus.PENDING.toString());
            notification.setSmsSentStatus(AppEnums.EventStatus.PENDING.toString());
            notification.setRetryCount(0);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setOrganizerId(Math.toIntExact(participant.getRegisteredSession().getCreatedBy().getId()));
            notification.setApplicationName(AppEnums.ApplicationName.MUMLY_TUTOR.toString());
            notification.setOwnerName(participant.getRegisteredSession().getCreatedBy().getFullName());
            adminNotificationRepository.save(notification);
        }
    }
}
