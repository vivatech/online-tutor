package com.vivatech.onlinetutor.controller;


import com.vivatech.mumly_event.notification.model.AdminNotification;
import com.vivatech.mumly_event.notification.repository.AdminNotificationRepository;
import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.Constants;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.notification.NotificationDto;
import com.vivatech.onlinetutor.notification.OnlineTutorNotificationService;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutor/notifications")
@Tag(name = "Notification", description = "Notification APIs")
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
    @Operation(summary = "Mark a notification as read", description = "The parent, student or tutor can mark a notification as read by passing id")
    public ResponseEntity<String> updateReadStatus(@PathVariable Integer id) {
        AdminNotification notification = adminNotificationRepository.findById(id).orElseThrow(() -> new OnlineTutorExceptionHandler("Notification not found"));
        notification.setRead(true);
        adminNotificationRepository.save(notification);
        return ResponseEntity.ok("Notification marked as read");
    }

    @GetMapping
    @Operation(summary = "Get notifications.", description = "The parent, student or tutor can view their notifications by passing username. " +
            "Pass isRead as true to get only read notifications and false to get unread notifications")
    public ResponseEntity<PaginationResponse<AdminNotification>> getNotifications(@RequestParam(name = "username") String username,
                                                                    @RequestParam(name = "isRead", required = false, defaultValue = "false") Boolean isRead,
                                                                    @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                                    @RequestParam(name = "size", required = false, defaultValue = Constants.PAGE_SIZE) Integer size) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
        List<AdminNotification> adminNotification = adminNotificationRepository.findByOrganizerIdAndIsReadAndApplicationName(Math.toIntExact(user.getId()), isRead, AppEnums.ApplicationName.MUMLY_TUTOR.toString());
        List<AdminNotification> notifications = adminNotification.stream().filter(ele -> !ele.getType().equals(AppEnums.NotificationType.EMERGENCY)).toList();
        Page<AdminNotification> pageList = CustomUtils.convertListToPage(notifications, pageNumber, size);
        PaginationResponse<AdminNotification> paginationResponse = CustomUtils.convertPageToPaginationResponse(pageList, notifications);
        if (paginationResponse.getContent().isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(paginationResponse);
    }

    @PostMapping("/emergency-notification")
    public ResponseEntity<String> sendEmergencyNotification(@RequestBody NotificationDto dto) {
        onlineTutorNotificationService.sendAdminNotification(dto.getSessionId(), AppEnums.NotificationType.EMERGENCY, dto.getMessage());
        return ResponseEntity.ok("Notification created successfully. Sending in the background.");
    }
}
