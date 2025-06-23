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
@Table(name = "mumly_tutor_payment")
public class MumlyTutorPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String msisdn;

    private Double amount;

    private String transactionId;

    private String referenceNo;

    private String paymentMode;

    private String paymentStatus; // e.g. SUCCESS, PENDING, FAILED
    private String reason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    private SessionRegistration sessionRegistration;

}
