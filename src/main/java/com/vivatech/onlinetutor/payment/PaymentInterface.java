package com.vivatech.onlinetutor.payment;

import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;

public interface PaymentInterface {
    boolean supports(AppEnums.PaymentMode paymentMode);

    Response sendPayment(PaymentDto paymentDto);

    Response reversePayment(PaymentDto dto);
}
