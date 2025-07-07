package com.vivatech.onlinetutor.controller;


import com.vivatech.mumly_event.notification.model.AdminNotification;
import com.vivatech.mumly_event.notification.repository.AdminNotificationRepository;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.notification.NotificationDto;
import com.vivatech.onlinetutor.notification.OnlineTutorNotificationService;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/notifications")
public class NotificationController {

    private final AdminNotificationRepository adminNotificationRepository;
    private final UserRepository userRepository;
    private final OnlineTutorNotificationService onlineTutorNotificationService;

    public NotificationController(AdminNotificationRepository adminNotificationRepository,
                                  UserRepository userRepository, OnlineTutorNotificationService onlineTutorNotificationService) {
        this.adminNotificationRepository = adminNotificationRepository;
        this.userRepository = userRepository;
        this.onlineTutorNotificationService = onlineTutorNotificationService;
    }


    @GetMapping("/update-read-status/{id}")
    public ResponseEntity<String> updateReadStatus(@PathVariable Integer id) {
        AdminNotification notification = adminNotificationRepository.findById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Notification not found"));
        notification.setRead(true);
        adminNotificationRepository.save(notification);
        return ResponseEntity.ok("Notification marked as read");
    }

    @GetMapping
    public ResponseEntity<List<AdminNotification>> getNotifications(@RequestParam(name = "username") String username,
                                                    @RequestParam(name = "isRead", required = false, defaultValue = "false") Boolean isRead) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
        List<AdminNotification> adminNotification = adminNotificationRepository.findByOrganizerIdAndIsReadAndApplicationName(Math.toIntExact(user.getId()), isRead, AppEnums.ApplicationName.MUMLY_TUTOR.toString());
        List<AdminNotification> notifications = adminNotification.stream().filter(ele -> !ele.getType().equals(AppEnums.NotificationType.EMERGENCY)).toList();
        if (notifications.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/emergency-notification")
    public ResponseEntity<String> sendEmergencyNotification(@RequestBody NotificationDto dto) {
        onlineTutorNotificationService.sendAdminNotification(dto.getSessionId(), AppEnums.NotificationType.EMERGENCY, dto.getMessage());
        return ResponseEntity.ok("Notification created successfully. Sending in the background.");
    }
}
