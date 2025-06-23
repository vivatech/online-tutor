package com.vivatech.onlinetutor.payment;

import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CashPayment implements PaymentInterface {

    @Autowired
    private MumlyTutorPaymentRepository paymentRepository;

    @Override
    public boolean supports(AppEnums.PaymentMode paymentMode) {
        return paymentMode.equals(AppEnums.PaymentMode.CASH);
    }

    @Override
    public Response sendPayment(PaymentDto paymentDto) {
        return Response.builder().status(AppEnums.PaymentStatus.SUCCESS.toString()).data(CustomUtils.generateRandomString()).build();
    }

    @Override
    public Response reversePayment(PaymentDto dto) {
        return null;
    }
}
