package com.vivatech.onlinetutor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "session_registrations")
public class SessionRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String studentAge;
    private String guardianName;
    @ManyToOne
    private TutorSession registeredSession;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
}
