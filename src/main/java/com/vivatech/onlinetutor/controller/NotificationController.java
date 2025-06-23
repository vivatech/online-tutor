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
    public Response updateReadStatus(@PathVariable Integer id) {
        AdminNotification notification = adminNotificationRepository.findById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Notification not found"));
        notification.setRead(true);
        adminNotificationRepository.save(notification);
        return Response.builder().status("SUCCESS").message("Notification marked as read").build();
    }

    @GetMapping
    public List<AdminNotification> getNotifications(@RequestParam(name = "username") String username,
                                                    @RequestParam(name = "isRead", required = false, defaultValue = "false") Boolean isRead) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
        List<AdminNotification> adminNotification = adminNotificationRepository.findByOrganizerIdAndIsReadAndApplicationName(Math.toIntExact(user.getId()), isRead, AppEnums.ApplicationName.MUMLY_TUTOR.toString());
        return adminNotification.stream().filter(ele -> !ele.getType().equals(AppEnums.NotificationType.EMERGENCY)).toList();
    }

    @PostMapping("/emergency-notification")
    public Response sendEmergencyNotification(@RequestBody NotificationDto dto) {
        onlineTutorNotificationService.sendAdminNotification(dto.getSessionId(), AppEnums.NotificationType.EMERGENCY, dto.getMessage());
        return Response.builder().status("SUCCESS").message("Notification created successfully. Sending in the background.").build();
    }
}
