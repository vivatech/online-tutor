package com.vivatech.onlinetutor.repository;

import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.model.MumlyTutorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MumlyTutorPaymentRepository extends JpaRepository<MumlyTutorPayment, Integer> {
    MumlyTutorPayment findByReferenceNo(String referenceNo);

    MumlyTutorPayment findBySessionRegistration(SessionRegistration registration);

    MumlyTutorPayment findByTransactionId(String transactionId);
}
