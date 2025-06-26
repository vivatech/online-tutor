package com.vivatech.onlinetutor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mumly_tutor_payout")
public class MumlyTutorPayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private TutorSession tutorSession;
    private Double amount;
    private Double commission;
    private Double netAmount;
    private String paymentStatus;
    private String transactionId;
    private String referenceNo;
    private String paymentMode;
    private String reason;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
