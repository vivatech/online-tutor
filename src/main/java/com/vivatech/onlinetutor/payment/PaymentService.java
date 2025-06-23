package com.vivatech.onlinetutor.payment;


import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.model.MumlyTutorPayment;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.notification.OnlineTutorNotificationService;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentGatewayProcessor paymentGateway;

    @Autowired
    private MumlyTutorPaymentRepository paymentRepository;

    @Autowired
    private OnlineTutorNotificationService notificationService;

    public Response processPayment(PaymentDto paymentDto) {
        Response response = new Response();
        log.info("Processing payment: {}", paymentDto);
        MumlyTutorPayment sessionPayment = saveNewPaymentEntity(paymentDto);
        paymentDto.setReferenceNo(sessionPayment.getReferenceNo());
        if (paymentDto.getPaymentMode() == null) throw new OnlineTutorExceptionHandler("Payment mode not found");
        Response paymentResponse = paymentGateway.sendPayment(paymentDto, paymentDto.getPaymentMode());
        if (paymentResponse.getStatus().equalsIgnoreCase(AppEnums.PaymentStatus.SUCCESS.toString())) {
            String merchantReferenceNumber = (String) paymentResponse.getData();
            sessionPayment.setReferenceNo(merchantReferenceNumber);
            response.setStatus(AppEnums.PaymentStatus.SUCCESS.toString());
        } else {
            sessionPayment.setPaymentStatus(AppEnums.PaymentStatus.FAILED.toString());
            sessionPayment.setReason(paymentResponse.getMessage());
            sessionPayment.getSessionRegistration().setStatus(AppEnums.EventStatus.FAILED.toString());
            response.setStatus(AppEnums.PaymentStatus.FAILED.toString());
        }
        MumlyTutorPayment savedPayment = paymentRepository.save(sessionPayment);
        response.setMessage("Reference No: " + savedPayment.getReferenceNo());
        response.setData(savedPayment.getReferenceNo());
        notificationService.sendAdminNotification(savedPayment.getId(), AppEnums.NotificationType.PAYMENT, null);
        return response;
    }

    public void processPaymentCallBack(String referenceNo, String transactionId, String paymentStatus, String reason) {
        MumlyTutorPayment payment = paymentRepository.findByReferenceNo(referenceNo);
        if (payment == null) throw new OnlineTutorExceptionHandler("Payment not found");
        payment.setTransactionId(transactionId);
        payment.setPaymentStatus(paymentStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setReason(reason);
        paymentRepository.save(payment);
    }

    public MumlyTutorPayment saveNewPaymentEntity(PaymentDto paymentDto) {
        MumlyTutorPayment payment = new MumlyTutorPayment();
        payment.setMsisdn(paymentDto.getMsisdn());
        payment.setAmount(paymentDto.getAmount());
        payment.setReferenceNo(CustomUtils.generateRandomString());
        payment.setPaymentMode(paymentDto.getPaymentMode().toString());
        payment.setPaymentStatus(AppEnums.PaymentStatus.PENDING.toString());
        payment.setCreatedAt(LocalDateTime.now());
        SessionRegistration sessionRegistration = new SessionRegistration();
        sessionRegistration.setId(paymentDto.getSessionRegistrationId());
        payment.setSessionRegistration(sessionRegistration);
        return paymentRepository.save(payment);
    }

    public Response refundTicket(PaymentDto dto) {
        MumlyTutorPayment payment = paymentRepository.findByTransactionId(dto.getTransactionId());
        dto.setPaymentMode(AppEnums.PaymentMode.valueOf(payment.getPaymentMode()));
        dto.setMsisdn(payment.getMsisdn());
        dto.setSessionRegistrationId(payment.getSessionRegistration().getId());

        MumlyTutorPayment refundPayment = saveNewPaymentEntity(dto);
        refundPayment.setReferenceNo(payment.getTransactionId());

        Response response = paymentGateway.refundPayment(dto, dto.getPaymentMode());
        if (response.getStatus().equalsIgnoreCase(AppEnums.PaymentStatus.SUCCESS.toString())) {
            refundPayment.setTransactionId((String) response.getData());
            refundPayment.setPaymentStatus(AppEnums.PaymentStatus.REFUND.toString());
            refundPayment.setUpdatedAt(LocalDateTime.now());
            refundPayment.setReason(dto.getReason());
        } else {
            refundPayment.setPaymentStatus(AppEnums.PaymentStatus.REFUND_FAILED.toString());
            refundPayment.setReason(response.getMessage());
        }
        paymentRepository.save(refundPayment);
        return response;
    }
}
